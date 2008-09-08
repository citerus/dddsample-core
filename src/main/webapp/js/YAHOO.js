/*
Copyright (c) 2006 Yahoo! Inc. All rights reserved.
version 0.9.0
*/

/**
 * @class The Yahoo global namespace
 */
var YAHOO = function() {

    return {

        /**
         * Yahoo presentation platform utils namespace
         */
        util: {},

        /**
         * Yahoo presentation platform widgets namespace
         */
        widget: {},

        /**
         * Yahoo presentation platform examples namespace
         */
        example: {},

        /**
         * Returns the namespace specified and creates it if it doesn't exist
         *
         * YAHOO.namespace("property.package");
         * YAHOO.namespace("YAHOO.property.package");
         *
         * Either of the above would create YAHOO.property, then
         * YAHOO.property.package
         *
         * @param  {String} sNameSpace String representation of the desired
         *                             namespace
         * @return {Object}            A reference to the namespace object
         */
        namespace: function( sNameSpace ) {

            if (!sNameSpace || !sNameSpace.length) {
                return null;
            }

            var levels = sNameSpace.split(".");

            var currentNS = YAHOO;

            // YAHOO is implied, so it is ignored if it is included
            for (var i=(levels[0] == "YAHOO") ? 1 : 0; i<levels.length; ++i) {
                currentNS[levels[i]] = currentNS[levels[i]] || {};
                currentNS = currentNS[levels[i]];
            }

            return currentNS;

        }
    };

} ();

