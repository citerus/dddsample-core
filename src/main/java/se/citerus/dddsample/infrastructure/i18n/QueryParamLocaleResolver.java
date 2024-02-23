package se.citerus.dddsample.infrastructure.i18n;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class QueryParamLocaleResolver extends AbstractLocaleContextResolver {
    private List<Locale> supportedLocales;

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        String[] locales = request.getParameterMap().get("lang");
        String localeName;
        if (locales != null && locales.length > 0) {
            localeName = locales[0];
        } else {
            return new SimpleLocaleContext(getDefaultLocale());
        }

        Locale requestLocale = convertLocaleName(localeName);
        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return new SimpleLocaleContext(requestLocale);
        }

        Locale supportedLocale = findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return new SimpleLocaleContext(supportedLocale);
        }
        return new SimpleLocaleContext(getDefaultLocale() != null ? getDefaultLocale() : requestLocale);
    }

    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        Enumeration<Locale> requestLocales = request.getLocales();
        Locale languageMatch = null;
        while (requestLocales.hasMoreElements()) {
            Locale locale = requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    // Full match: language + country, possibly narrowed from earlier language-only match
                    return locale;
                }
            }
            else if (languageMatch == null) {
                // Let's try to find a language-only match as a fallback
                for (Locale candidate : supportedLocales) {
                    if (!StringUtils.hasLength(candidate.getCountry()) &&
                            candidate.getLanguage().equals(locale.getLanguage())) {
                        languageMatch = candidate;
                        break;
                    }
                }
            }
        }
        return languageMatch;
    }

    private Locale convertLocaleName(String localeName) {
        return localeName.contains("_") ? new Locale(localeName.split("_")[0], localeName.split("_")[1]) : new Locale(localeName);
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        // intentionally left blank
    }

    public List<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(List<Locale> supportedLocales) {
        this.supportedLocales = supportedLocales;
    }
}
