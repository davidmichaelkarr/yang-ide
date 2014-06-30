package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class YangStringScanner extends AbstractYangScanner {

    private static String[] tokenProperties= {
        IYangColorConstants.YANG_STRING
    };        
    
    /**
     * @param manager
     * @param store
     */
    public YangStringScanner(IColorManager manager, IPreferenceStore store) {
        super(manager, store);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cisco.yangide.editor.editors.AbstractYangScanner#getTokenProperties()
     */
    @Override
    protected String[] getTokenProperties() {
        return tokenProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cisco.yangide.editor.editors.AbstractYangScanner#createRules()
     */
    @Override
    protected List<IRule> createRules() {
        List<IRule> rules = new ArrayList<IRule>();
        
        IToken string = getToken(IYangColorConstants.YANG_STRING);
        // Add rule for double quotes
        rules.add(new MultiLineRule("\"", "\"", string, '\\'));
        // Add a rule for single quotes
        rules.add(new MultiLineRule("'", "'", string, '\\'));
        
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new YangWhitespaceDetector()));

        return rules;
    }
}
