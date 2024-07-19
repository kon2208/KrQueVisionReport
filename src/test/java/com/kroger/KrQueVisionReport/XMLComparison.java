package com.kroger.KrQueVisionReport;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

class IgnoreAttributeDifferenceEvaluator implements DifferenceEvaluator {
    private String attributeName;

    public IgnoreAttributeDifferenceEvaluator(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
        if (outcome == ComparisonResult.EQUAL) return outcome; // only evaluate differences.
        final Node controlNode = comparison.getControlDetails().getTarget();
        if (controlNode instanceof Attr) {
            Attr attr = (Attr) controlNode;
            if (attr.getName().equals(attributeName)) {
                return ComparisonResult.SIMILAR; // will evaluate this difference as similar
            }
        }
        return outcome;
    }
}

public class XMLComparison {
    @Test
    public static String XMLValidation(String expected, String actual) {
        Diff myDiff = DiffBuilder.compare(expected).withTest(actual)
                .normalizeWhitespace()
                .withDifferenceEvaluator(DifferenceEvaluators.chain(
                    (new IgnoreAttributeDifferenceEvaluator("CreationDateTime")),
                    (new IgnoreAttributeDifferenceEvaluator("Payload"))


                ))
                .withNodeMatcher((new DefaultNodeMatcher(ElementSelectors.byName))).checkForSimilar().build();

        System.out.println("Difference " + myDiff.toString());
        String XML_difference = myDiff.toString();
        Assert.assertFalse(myDiff.hasDifferences());
        return XML_difference;
    }
}
