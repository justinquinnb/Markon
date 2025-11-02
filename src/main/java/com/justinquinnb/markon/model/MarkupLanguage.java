package com.justinquinnb.markon.model;

import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;

/**
 * A markup language, defined by its parsing and application rules
 */
public interface MarkupLanguage {
    ParsingRuleset getParsingRuleset();
    ApplicationRuleset getApplicationRuleset();
}
