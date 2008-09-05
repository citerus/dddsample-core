function calendar_constructor() {
	var DEFAULT_LANGUAGE = "en";

	var WEEK_DAYS_LABELS = { 
		en : [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" ],
		pl : [ "Ni", "Po", "Wt", "&#x015A;r", "Cz", "Pt", "So" ]
	};
	var MONTH_NAMES_LABELS = {
		en : [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ],
		pl : [ "Stycze&#x0144;", "Luty", "Marzec", "Kwiecie&#x0144;", "Maj", "Czerwiec", "Lipiec", "Sierpie&#x0144;", "Wrzesie&#x0144;", "Pa&#x017A;dziernik", "Listopad", "Grudzie&#x0144;" ]
	};
	
	var FORMATTERS = {
		en : function( cDate ) {
			return (cDate.getMonth()+1) + "/" + cDate.getDate() + "/" + cDate.getFullYear();
			},
		pl : function( cDate ) {
			return cDate.getDate() + "." + (cDate.getMonth()+1) + "." + cDate.getFullYear();
			}
	}
	var UNFORMATTERS = {
		en : function( stringDate ) {
				var elems = stringDate.split( "/" );
				if ( elems.length != 3 ) return null;
				for ( var i=0; i<elems.length; i++ ) {
					if ( isNaN( elems[ i ] ) ) return null;
				}
				var year = parseInt( elems[ 2 ] );
				if ( year < 100 ) {
					year += 2000;
				}
				return new Date( year,
					parseInt( elems[ 0 ] ) - 1,
					parseInt( elems[ 1 ] ) );
			},
		pl : function( stringDate ) {
				var elems = stringDate.split( "." );
				if ( elems.length != 3 ) return null;
				for ( var i=0; i<elems.length; i++ ) {
					if ( isNaN( elems[ i ] ) ) return null;
				}
				var year = parseInt( elems[ 2 ] );
				if ( year < 100 ) {
					year += 2000;
				}
				return new Date( year,
					parseInt( elems[ 1 ] ) - 1,
					parseInt( elems[ 0 ] ) );
			}
	}

	var invalidatorInitialized = false;
	var _this = this;
	
	this.calendarBox = null;
	
	this.hideCurrent = function( event, triggerElem ) {
		if ( triggerElem ) {
			if ( event ) { // if manual show/hide do not propagate normal behaviour
				YAHOO.util.Event.stopEvent( event );
			}
		}
		if ( this.calendarBox ) {
			this.calendarBox.style.display = "none";
			if ( this.calendarBox.triggerElem == triggerElem ) {
				this.calendarBox = null;
				return true;
			}
			this.calendarBox = null;
		}
		return false;
	};
	
	function cleanupMe( event ) {
		this.hideCurrent( event );
	}

	function cleanupMeKey( event ) {
		if ( event.keyCode == 27 ) {
			this.hideCurrent( event );
		} else {
			if ( this.calendarBox ) {
				if ( event.keyCode == 37 ) { // arrow left
					this.calendarBox.prev()
				} else if ( event.keyCode == 39 ) { // arrow right
					this.calendarBox.next()
				}
			}
		}
	}
	
	function initializeCleanups() {
		invalidatorInitialized = true;
		YAHOO.util.Event.addListener( document, "click", cleanupMe, _this, true );
		YAHOO.util.Event.addListener( document, "keypress", cleanupMeKey, _this, true );
	}
	
	this.toggle = function ( event, triggerElem, inputId ) {
	
		if ( this.hideCurrent( event, triggerElem ) ) {
			return false;
		}
		
		// set the date that is set as actual in input field
		var today = new Date();
		today = new Date( today.getFullYear(), today.getMonth(), today.getDate() );
		
		var input = document.getElementById( inputId );
		// (re)create the div container of calendar
		var calendarShowed = document.createElement( "div" );
		calendarShowed.input = input;
		calendarShowed.triggerElem = triggerElem;
		calendarShowed.className = "calendar";
		// obtain language code (ISO 639) to use for formatting and labels 
		calendarShowed.currentLang = document.getElementsByTagName( "html" )[ 0 ].lang;
		if ( !calendarShowed.currentLang ) {
			if ( navigator.userLanguage ) {
				// obtain language from browser settings
				calendarShowed.currentLang = navigator.userLanguage;
			}
		}
		// -- decode current date set in textfield if possible
		var unF = getLocalizedEntry( UNFORMATTERS, calendarShowed.currentLang );
		var activeDate = unF( getDateValue( calendarShowed ) );
		if ( !activeDate ) { // fallback if undecoding failed
			activeDate = today;
		}
		calendarShowed.activeDate = activeDate;
		
		this.calendarBox = calendarShowed;
		
		var elementRegion = YAHOO.util.Dom.getRegion( triggerElem );
		calendarShowed.style.top = ( elementRegion.bottom ) + "px";
		calendarShowed.style.left = ( elementRegion.left ) + "px";
	
		//var calendarShowed = this.calendarShowed;
		// fill the contents of calendar for a month taken from actual date	
		calendarShowed.innerHTML = createContents( calendarShowed, 
			activeDate.getFullYear(), activeDate.getMonth(), activeDate.getDate() );
		
		var _this = this;
		calendarShowed.prev = function() {
			calendarShowed.currentMonth.setMonth( calendarShowed.currentMonth.getMonth() - 1 );
			calendarShowed.innerHTML = createContents( calendarShowed );
		};
		calendarShowed.next = function() {
			calendarShowed.currentMonth.setMonth( calendarShowed.currentMonth.getMonth() + 1 );
			calendarShowed.innerHTML = createContents( calendarShowed );
		};
		calendarShowed.select = function( tdElem ) {
			calendarShowed.currentMonth.setDate( parseInt( tdElem.innerHTML ) );
			var formattingF = getLocalizedEntry( FORMATTERS, calendarShowed.currentLang );
			var inputToStoreValue = setDateValue( calendarShowed, formattingF( calendarShowed.currentMonth ) );
			inputToStoreValue.focus();
		}
		
		// add the calendar to document for display
		document.getElementsByTagName("body")[0].appendChild( calendarShowed );

		if ( !invalidatorInitialized ) {
			initializeCleanups();
		}

		return false;
	};
	
	function setDateValue( calendar, stringValue ) {
		var inputToStoreValue = obtainInputField( calendar );
		inputToStoreValue.value = stringValue;
		return inputToStoreValue;
	}

	function getDateValue( calendar ) {
		var inputToStoreValue = obtainInputField( calendar );
		return inputToStoreValue.value;
	}

	// very private function
	function obtainInputField( calendar ) {
		var inputToStoreValue = null;
		if ( calendar.input.dynamicId ) {
			inputToStoreValue = document.getElementById( calendar.input.dynamicId );
			if ( inputToStoreValue ) { // if found not a text input - use default one
				//alert( inputToStoreValue.tagName );
				if ( inputToStoreValue.tagName != "INPUT" ) {
					inputToStoreValue = null; 
				}
			}
		}
		if ( !inputToStoreValue ) { // fallback
			inputToStoreValue = calendar.input;
		}
		return inputToStoreValue;
	}



	function getLocalizedEntry( arr, language ) {
		var entry = arr[ language ];
		if ( !entry ) {
			entry = arr[ DEFAULT_LANGUAGE ];
		}
		return entry;
	}

	function createContents( elem, year, month, day ) { // year, month, day are optional in nonfirst calls
		if ( elem.currentMonth ) {
			year = elem.currentMonth.getFullYear();
			month = elem.currentMonth.getMonth();
			day = elem.currentMonth.getDate();
		} else {
			elem.currentMonth = new Date( year, month, 1 );
		}
		
		var dateC = new Date( elem.currentMonth.getFullYear(), elem.currentMonth.getMonth(), 1 );
		var today = new Date();
		today = new Date( today.getFullYear(), today.getMonth(), today.getDate() );
		
		var iHtml = "<table>\n";
		iHtml += "<thead><tr>";
		iHtml += "<td class=\"goPrevious\" onclick=\"calendar.calendarBox.prev()\"></td>";
		iHtml += "<td colspan=\"5\">";
		iHtml += getDateHeaderLabel( elem.currentLang, dateC );
		iHtml += "</td>";
		iHtml += "<td class=\"goNext\" onclick=\"calendar.calendarBox.next()\"></td>";
		iHtml += "</tr>";
		iHtml += "<tr>";
		var wDL = getWeekDaysLabels( elem.currentLang );
		for ( var i=0; i<wDL.length; i++ ) {
			iHtml += "<td>" + wDL[ i ] + "</td>";
		}
		iHtml += "</tr>";
		iHtml += "</thead>";
	
		iHtml += "<tbody>\n";
		var dayOfWeek = dateC.getDay(); // 0 - sunday, ..
		iHtml += "<tr>";
		if ( dayOfWeek > 0 ) {
			dateC.setDate( -dayOfWeek + 1 );
			for ( var i=0; i<dayOfWeek; i++ ) {
				iHtml += "<td class=\"disabled\">" + dateC.getDate() + "</td>";
				dateC.setDate( dateC.getDate() + 1 );
			}
		}
		var accu = dayOfWeek;
		var tdClass = null;
		while ( dateC.getMonth() == month ) {
			if ( accu % 7 == 0 ) {
				accu = 0;
				iHtml += "<tr>";
			}
			// note: simple equality checking is not sufficient here as it will check for reference equality
			if (!( dateC > elem.activeDate ) && !( dateC < elem.activeDate )) {
				tdClass = "active";
			} else {
				if ( dateC < today ) {
					tdClass = "past";
				} else if ( dateC > today ) {
					tdClass = "future";
				} else {
					tdClass = "today";
				}
			}
			iHtml += "<td class='" + tdClass + "' onclick=\"calendar.calendarBox.select(this)\">" + dateC.getDate() + "</td>";
			dateC.setDate( dateC.getDate() + 1 );
			accu++;
	
			if ( accu % 7 == 0 ) {
				iHtml += "</tr>\n";
			}
		}
		//..
		dayOfWeek = dateC.getDay();
		if ( dayOfWeek != 0 ) {
			for ( var i=dayOfWeek; i<7; i++ ) {
				iHtml += "<td class=\"disabled\">" + dateC.getDate() + "</td>";
				dateC.setDate( dateC.getDate() + 1 );
			}
			iHtml += "</tr>\n";
		}
		
		iHtml += "</tbody>";
		iHtml += "</table>";
		return iHtml;
	};
	
	function getWeekDaysLabels( languageCode ) {
		return getLocalizedEntry( WEEK_DAYS_LABELS, languageCode );
	}
	
	function getDateHeaderLabel( languageCode, curDate ) {
		var labels = getLocalizedEntry( MONTH_NAMES_LABELS, languageCode );
		return labels[ curDate.getMonth() ] + "&nbsp;" + curDate.getFullYear();
	};

}
calendar = new calendar_constructor();