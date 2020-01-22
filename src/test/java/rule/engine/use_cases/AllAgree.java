package rule.engine.use_cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import model.Credibility;
import model.ModelProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import rule.engine.PolicyEngineCallback;
import rule.engine.RuleEngine;
import rule.engine.RuleEngineFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

// All agree, but some have low confidence

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AllAgree {

    RuleEngine ruleEngine;
    Set<String> ruleSet;
    Map<String, Object> moduleResponses;
    PolicyEngineCallback callback;
    Credibility expectedLabel;

    @Before
    public void setUp() throws IOException {
        ruleEngine = RuleEngineFactory.newInstance("jeasy");
        ruleSet = new HashSet<>();
        ruleSet.add("misinfome");
        ruleSet.add("claimcredibility");
        ruleSet.add("contentanalysis");
        moduleResponses = new LinkedHashMap<>();
        callback =  new PolicyEngineCallback();
    }

    @Test
    public void case_credible_1() {
        //edit these values
        moduleResponses.put("misinfome_credibility_value", 1);
        moduleResponses.put("misinfome_credibility_confidence", 1);
        moduleResponses.put("contentanalysis_credibility", 1);
        moduleResponses.put("contentanalysis_confidence", 1);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_credibility", 1);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_confidence", 0.1);
        expectedLabel = Credibility.credible;

        eval_rule();
    }

    protected void eval_rule() {
        ruleEngine.check(new ModelProperties(moduleResponses), callback, ruleSet);

        //some log printouts
        log.info("module labels:");
        callback.getModuleCredibility().forEach((name, label) -> log.info("\t{}: {}", name, label));
        log.info("expected label: {}, actual label: {}", expectedLabel, callback.getFinalCredibility());

        assertThat(callback.getFinalCredibility()).isEqualTo(expectedLabel);
    }


    @Test
    public void case_mostly_credible() {
        //edit these values
        moduleResponses.put("misinfome_credibility_value", 0.55);
        moduleResponses.put("misinfome_credibility_confidence", 0.3);
        moduleResponses.put("contentanalysis_credibility", 0.4);
        moduleResponses.put("contentanalysis_confidence", 0.1);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_credibility", 0.4);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_confidence", 0.9);
        expectedLabel = Credibility.mostly_credible;

        eval_rule();
    }

    @Test
    public void case_credible_uncertain() {
        //edit these values
        moduleResponses.put("misinfome_credibility_value", 0.25);
        moduleResponses.put("misinfome_credibility_confidence", 0.1);
        moduleResponses.put("contentanalysis_credibility", -0.2);
        moduleResponses.put("contentanalysis_confidence", 0.1);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_credibility", -0.2);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_confidence", 0.8);
        expectedLabel = Credibility.credible_uncertain;

        eval_rule();
    }

    @Test
    public void mostly_not_credible() {
        //edit these values
        moduleResponses.put("misinfome_credibility_value", -0.5);
        moduleResponses.put("misinfome_credibility_confidence", 1);
        moduleResponses.put("contentanalysis_credibility", -0.4);
        moduleResponses.put("contentanalysis_confidence", 0.8);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_credibility", -0.4);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_confidence", 0.1);
        expectedLabel = Credibility.mostly_not_credible;

        eval_rule();
    }

    @Test
    public void not_credible() {
        //edit these values
        moduleResponses.put("misinfome_credibility_value", -0.7);
        moduleResponses.put("misinfome_credibility_confidence", 0.2);
        moduleResponses.put("contentanalysis_credibility", -0.65);
        moduleResponses.put("contentanalysis_confidence", 0.3);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_credibility", -0.6);
        moduleResponses.put("claimcredibility_tweet_claim_credibility_0_confidence", 0.9);
        expectedLabel = Credibility.mostly_not_credible;

        eval_rule();
    }

}
