package com.flaredown.flaredownApp.Helpers.API.EntryParser;



import com.flaredown.flaredownApp.BuildConfig;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by thunter on 23/02/16.
 */
@Config(sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class EntryTest extends TestCase{
    private static JSONObject createDefaultJSONObject() throws JSONException {
        //Standard response from the entry end point.
        String defaultEntryEnpointResponse = "{\"entry\":{\"id\":\"55f94c46504420000b00002c\",\"date\":\"Sep-16-2015\",\"catalog_definitions\":{\"hbi\":[[{\"name\":\"general_wellbeing\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"very_well\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"slightly_below_par\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"poor\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"very_poor\",\"meta_label\":\"\",\"helper\":null},{\"value\":4,\"label\":\"terrible\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"ab_pain\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"none\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"mild\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"moderate\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"severe\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"stools\",\"kind\":\"number\",\"step\":1,\"min\":0,\"max\":100,\"inputs\":[{\"value\":0,\"label\":null,\"meta_label\":null,\"helper\":\"stools_daily\"}]}],[{\"name\":\"ab_mass\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"none\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"dubious\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"definite\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"definite_and_tender\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"complication_arthralgia\",\"kind\":\"checkbox\"},{\"name\":\"complication_uveitis\",\"kind\":\"checkbox\"},{\"name\":\"complication_erythema_nodosum\",\"kind\":\"checkbox\"},{\"name\":\"complication_aphthous_ulcers\",\"kind\":\"checkbox\"},{\"name\":\"complication_anal_fissure\",\"kind\":\"checkbox\"},{\"name\":\"complication_fistula\",\"kind\":\"checkbox\"},{\"name\":\"complication_abscess\",\"kind\":\"checkbox\"}]],\"rapid3\":[[{\"name\":\"dress_yourself\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"get_in_out_of_bed\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"lift_full_glass\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"walk_outdoors\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"wash_and_dry_yourself\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"bend_down\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"turn_faucet\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"enter_exit_vehicles\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"walk_two_miles\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"play_sports\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"label\":\"no_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":1,\"label\":\"some_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":2,\"label\":\"much_difficulty\",\"meta_label\":\"\",\"helper\":null},{\"value\":3,\"label\":\"unable\",\"meta_label\":\"\",\"helper\":null}]}],[{\"name\":\"pain_tolerance\",\"kind\":\"number\",\"inputs\":[{\"value\":0,\"step\":0.5,\"min\":0,\"label\":\"pain_tolerance\",\"meta_label\":null,\"helper\":null}]}],[{\"name\":\"global_estimate\",\"kind\":\"number\",\"inputs\":[{\"value\":0,\"step\":0.5,\"min\":0,\"label\":\"global_estimate\",\"meta_label\":null,\"helper\":null}]}]],\"symptoms\":[[{\"name\":\"droopy lips\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"fat toes\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"slippery tongue\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}]],\"conditions\":[[{\"name\":\"asdf\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"ARDS\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Asthma\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Broken toe\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Crohn's disease\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Croup\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Depression\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Hiccups\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Drunk\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Optic neuritis\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Q fever\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Rheumatoid arthritis\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"Rosacea\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}],[{\"name\":\"The Giggles\",\"kind\":\"select\",\"inputs\":[{\"value\":0,\"helper\":\"basic_0\",\"meta_label\":\"smiley\"},{\"value\":1,\"helper\":\"basic_1\",\"meta_label\":null},{\"value\":2,\"helper\":\"basic_2\",\"meta_label\":null},{\"value\":3,\"helper\":\"basic_3\",\"meta_label\":null},{\"value\":4,\"helper\":\"basic_4\",\"meta_label\":null}]}]]},\"catalogs\":[\"hbi\",\"rapid3\",\"symptoms\"],\"notes\":\"\",\"complete\":false,\"tags\":[],\"created_at\":\"2015-09-16T11:02:30.576Z\",\"updated_at\":\"2015-09-16T11:02:30.576Z\",\"just_created\":false,\"responses\":[{\"id\":\"hbi_general_wellbeing_56bdd9ceacacf30008000000\",\"name\":\"general_wellbeing\",\"value\":null,\"catalog\":\"hbi\"},{\"id\":\"hbi_ab_pain_56bdd9ceacacf30008000000\",\"name\":\"ab_pain\",\"value\":null,\"catalog\":\"hbi\"},{\"id\":\"hbi_stools_56bdd9ceacacf30008000000\",\"name\":\"stools\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_ab_mass_56bdd9ceacacf30008000000\",\"name\":\"ab_mass\",\"value\":1.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_arthralgia_56bdd9ceacacf30008000000\",\"name\":\"complication_arthralgia\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_uveitis_56bdd9ceacacf30008000000\",\"name\":\"complication_uveitis\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_erythema_nodosum_56bdd9ceacacf30008000000\",\"name\":\"complication_erythema_nodosum\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_aphthous_ulcers_56bdd9ceacacf30008000000\",\"name\":\"complication_aphthous_ulcers\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_anal_fissure_56bdd9ceacacf30008000000\",\"name\":\"complication_anal_fissure\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_fistula_56bdd9ceacacf30008000000\",\"name\":\"complication_fistula\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"hbi_complication_abscess_56bdd9ceacacf30008000000\",\"name\":\"complication_abscess\",\"value\":0.0,\"catalog\":\"hbi\"},{\"id\":\"rapid3_dress_yourself_3982032jkjlj32kj\",\"name\":\"dress_yourself\",\"value\":2.0,\"catalog\":\"rapid3\"},{\"id\":\"conditions_broken_toe_r43jk34khkj43\",\"name\":\"Broken toe\",\"value\":3.0,\"catalog\":\"conditions\"},{\"id\":\"symptoms_droopy_lips_dkljfsdf98sd09f8\",\"name\":\"droopy lips\",\"value\":0.0,\"catalog\":\"symptoms\"}],\"treatments\":[{\"id\":\"Alora___1_55f94c46504420000b00002c\",\"name\":\"Alora\",\"quantity\":null,\"unit\":null},{\"id\":\"P-a-c analgesic___1_55f94c46504420000b00002c\",\"name\":\"P-a-c analgesic\",\"quantity\":null,\"unit\":null},{\"id\":\"Tickles___1_55f94c46504420000b00002c\",\"name\":\"Tickles\",\"quantity\":null,\"unit\":null},{\"id\":\"Laughing Gas___1_55f94c46504420000b00002c\",\"name\":\"Laughing Gas\",\"quantity\":null,\"unit\":null}]}}";
        return new JSONObject(defaultEntryEnpointResponse);
    }

    /**
     * Test the creation of an entry by passing a JSON object.
     * @throws Exception
     */
    @Test
    public void testConstructorPassingJSON() throws Exception{
        Entry entry = new Entry(createDefaultJSONObject());
    }

    /**
     * Counts the number of collections, along with the number of catalogs and ensures there is the
     * correct amount for the test data.
     * @throws Exception
     */
    @Test
    public void testCollectionCount() throws Exception{
        Entry entry = new Entry(createDefaultJSONObject());

        int hbiCount = 0;
        int rapid3Count = 0;
        int symptomsCount = 0;
        int conditionsCount = 0;
        for (CollectionCatalogDefinition catalogDefinitions : entry) {
            switch (catalogDefinitions.catalogName) {
                case "hbi":
                    hbiCount++;
                    break;
                case "rapid3":
                    rapid3Count++;
                    break;
                case "symptoms":
                    symptomsCount++;
                    break;
                case "conditions":
                    conditionsCount++;
                    break;
            }
        }

        assertEquals("Number of catalog definition collections", 34, entry.size());
        assertEquals("Number of hbi entries", hbiCount, 5);
        assertEquals("Number of rapid3 entries", rapid3Count, 12);
        assertEquals("Number of symptoms", symptomsCount, 3);
        assertEquals("Number of conditions", conditionsCount, 14);
    }

    /**
     * Counts the total number of CatalogDefinition objects and ensures it is the correct amount.
     * @throws Exception
     */
    @Test
    public void testCatalogDefinitionCount() throws Exception {
        Entry entry = new Entry(createDefaultJSONObject());
        int totalCDCount = 0;
        for (CollectionCatalogDefinition catalogDefinitions : entry) {
            for (CatalogDefinition catalogDefinition : catalogDefinitions) {
                totalCDCount++;
                System.out.println(catalogDefinition.getCatalogName() + " : " + catalogDefinition.getDefinitionName());
            }
        }
        assertEquals("Total catalog definitions", 40, totalCDCount);
    }

    /**
     * Makes sure method returns correct JSON Object.
     * @throws Exception
     */
    @Test
    public void testToJSONObject() throws Exception {
        Entry entry = new Entry(createDefaultJSONObject());
        System.out.println(entry.toJSONObject().toString(4));
        fail("Manual Checking is needed at this point");
    }
}