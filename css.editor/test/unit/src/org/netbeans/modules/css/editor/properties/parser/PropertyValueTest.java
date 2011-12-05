/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.css.editor.properties.parser;

import org.netbeans.modules.css.editor.module.CssModuleSupport;
import org.netbeans.modules.css.editor.module.main.CssModuleTestBase;

/**
 *
 * @author mfukala@netbeans.org
 */
public class PropertyValueTest extends CssModuleTestBase {

    public PropertyValueTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
//        GrammarResolver.setLogging(GrammarResolver.Log.DEFAULT, true);
//        GrammarResolver.setLogging(GrammarResolver.Log.ALTERNATIVES, true);
//        PRINT_INFO_IN_ASSERT_RESOLVE = true;
    }
    
    

    public void testAlternativesComplicated1() {
        String grammar1 = "[ marek  jitka  [ [ ovecka | bubu ]? nee ] ] | [ marek jitka [ tobik | bibik ] ] ";
        assertAlternatives(grammar1, "marek jitka", "ovecka", "bubu", "nee", "tobik", "bibik");
    }

    public void testAlternativesComplicated2() {
        String grammar2 = "[ marek  jitka  [ [ ovecka | bubu ]? nee ] ] | [ marek [ jitka | x ] [ tobik | bibik ] ]  | [ marek tohlene ]";
        assertAlternatives(grammar2, "marek jitka tobik");
    }

    public void testAlternativesComplicated21() {
        String grammar2 = "[ marek  jitka  [ [ ovecka | bubu ]? nee ] ] | [ marek [ jitka | x ] [ tobik | bibik ] ]  | [ marek tohlene ]";
        assertAlternatives(grammar2, "marek jitka", "ovecka", "bubu", "nee", "tobik", "bibik");
    }

    public void testAlternativesComplicated3() {
        String grammar2 = "[ [ a b ] ] | [ a c ]";

        assertAlternatives(grammar2, "a", "b", "c");
        assertAlternatives(grammar2, "a b");
    }

    public void testAlternativesComplicated4() {
        String grammar2 = "[ a b c  ] | [ a X ]";
        assertAlternatives(grammar2, "a b", "c");
    }

    public void testAlternativesComplicated41() {
        String grammar2 = "[ a b C ] | [ a b D ]  | [ a E ]";
        assertAlternatives(grammar2, "a b", "C", "D");
    }

    public void testAlternativesSet() {
        //set
        assertAlternatives("a | b | c", "", "a", "b", "c");
        assertAlternatives("a | b | c", "a");
        assertAlternatives("a | b | c", "b");
        assertAlternatives("a | b | c", "c");

    }

    public void testAlternativesList() {
        //list
        assertAlternatives("a || b || c", "", "a", "b", "c");
        assertAlternatives("a || b || c", "a", "b", "c");
        assertAlternatives("a || b || c", "b", "a", "c");
        assertAlternatives("a || b || c", "c", "a", "b");
        assertAlternatives("a || b || c", "a b", "c");
        assertAlternatives("a || b || c", "b a", "c");
        assertAlternatives("a || b || c", "a c", "b");
        assertAlternatives("a || b || c", "c a", "b");
        assertAlternatives("a || b || c", "b c", "a");
        assertAlternatives("a || b || c", "c b", "a");

    }

    public void testAlternativesSequence() {
        //sequence
        assertAlternatives("a b c", "", "a");
        assertAlternatives("a b c", "a", "b");
        assertAlternatives("a b c", "a b", "c");
        assertAlternatives("a b c", "a b c");
    }

    public void testAlternativesOfSequenceInSequence() {
        String grammar = "marek  jitka  [ ovecka  beranek ]";

        assertAlternatives(grammar, "marek jitka", "ovecka");
        assertAlternatives(grammar, "marek jitka ovecka", "beranek");
        assertAlternatives(grammar, "marek jitka beranek");
    }

    public void testFontAlternatives() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font");
        String text = "italic small-caps 30px";

        assertAlternatives(p.getGrammar(), text,
                "fantasy","serif","!string","sans-serif","monospace","/","!identifier","cursive");

    }

//    public void testVoiceFamily() {
//        PropertyModel p = CssModuleSupport.getPropertyModel("voice-family");
//        assertAlternatives(p.getGrammar(), "male", ",", "!integer");
//        assertAlternatives(p.getGrammar(), "male,", "old","!ident","!integer","!string","neutral","young","child","!ident","female","!integer","!string","male");
//
//        assertAlternatives(p.getGrammar(), "", "child","!ident","female","!integer","!string","male");
//    }
    public void testPaddingAlternatives() {
        PropertyModel p = CssModuleSupport.getPropertyModel("padding");
        assertAlternatives(p.getGrammar(), "", "!percentage", "!length");

    }

    public void testAlternativesInGroupMultiplicitySimple() {
        assertAlternatives("[ marek ]*", "marek", "marek");
        assertAlternatives("[ marek ]*", "marek marek", "marek");
    }

    public void testAlternativesInGroupMultiplicity() {
        assertAlternatives("[ marek | jitka ]*", "marek", "marek", "jitka");
        assertAlternatives("[ marek | jitka ]*", "jitka", "marek", "jitka");
        assertAlternatives("[ marek | jitka ]*", "jitka marek jitka", "marek", "jitka");

        assertAlternatives("[ marek jitka? ovecka ]*", "marek jitka", "ovecka");
        assertAlternatives("[ marek jitka? ovecka ]*", "marek ovecka", "marek");

    }

    public void testFontThoroughly() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font");
        assertAlternatives(p.getGrammar(), "20px", 
                "fantasy","serif","!string","sans-serif","monospace","/","!identifier","cursive");
        assertAlternatives(p.getGrammar(), "20px /",
                "initial","normal","none","!number","!length","!percentage");
        assertAlternatives(p.getGrammar(), "20px / 5pt",
                "fantasy","serif","!string","sans-serif","monospace","!identifier","cursive");
        assertAlternatives(p.getGrammar(), "20px / 5pt cursive", 
                ",");
    }

    public void testFontThoroughly2() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font");
        assertAlternatives(p.getGrammar(), "italic",
                "small-caps", "800", "normal", "lighter", "smaller", "600", "bold",
                "700", "!length", "xx-small", "bolder", "100", "300", "!percentage",
                "200", "larger", "medium", "500", "x-large", "x-small", "400",
                "xx-large", "900", "small", "large");

        assertAlternatives(p.getGrammar(), "italic large",
                "fantasy", "serif", "sans-serif", "monospace", "/", "cursive", "!string","!identifier");

        assertAlternatives(p.getGrammar(), "italic large / ",
                "!percentage", "initial", "normal", "!length", "none", "!number");

        assertAlternatives(p.getGrammar(), "italic large / normal",
                "fantasy", "serif", "sans-serif", "monospace", "cursive", "!string","!identifier");
    }

    public void testBackgroundRGBAlternatives() {
        PropertyModel p = CssModuleSupport.getPropertyModel("background");
        assertAlternatives(p.getGrammar(), "rgb", "(");
        assertAlternatives(p.getGrammar(), "rgb(", "!percentage", "!number");
        assertAlternatives(p.getGrammar(), "rgb(10%", ",");
        assertAlternatives(p.getGrammar(), "rgb(", "!percentage", "!number");
        assertAlternatives(p.getGrammar(), "rgb(10%, 20", ",");
        assertAlternatives(p.getGrammar(), "rgb(10%, 20, 6%", ")");
    }

    public void testAlternativesOfPartialyResolvedSequenceInListGroup() {
        String g = "[ [ Ema ma misu] || prd";
        assertAlternatives(g, "Ema", "ma");
        assertAlternatives(g, "Ema ma", "misu");
        assertAlternatives(g, "Ema ma misu", "prd");
        assertAlternatives(g, "prd", "Ema");
        assertAlternatives(g, "prd Ema", "ma");
        assertAlternatives(g, "prd Ema ma", "misu");
        assertAlternatives(g, "prd Ema ma misu");
    }

    public void testJindrasCase() {
        //#142254
        String g = "[ [ x || y ] || b";
        assertAlternatives(g, "x b"); //no alternatives
    }

    public void testFontFamily2() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font-family");

        assertAlternatives(p.getGrammar(), "",
                "fantasy", "serif", "sans-serif", "inherit", "monospace", "cursive", "!string","!identifier");

    }

    public void testFontFamilyInheritProblem() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font-family");
        assertAlternatives(p.getGrammar(), "inherit");
//        assertAlternatives(p.getGrammar(), "", "inherit");
    }

    public void testTheBorderCaseSimplified() {
        String g = " a || b || c";

        assertAlternatives(g, "", "a", "b", "c");
        assertAlternatives(g, "a", "b", "c");

        assertAlternatives(g, "a b", "c");
        assertAlternatives(g, "b a", "c");

        assertAlternatives(g, "b c", "a");
        assertAlternatives(g, "c b", "a");

        assertAlternatives(g, "c a", "b");
        assertAlternatives(g, "a c", "b");

        assertAlternatives(g, "a b c");
        assertAlternatives(g, "c b a");
        assertAlternatives(g, "a c b");
        assertAlternatives(g, "b a c");
    }

    public void testTheBorderCase() {
        PropertyModel p = CssModuleSupport.getPropertyModel("border");
        assertAlternatives(p.getGrammar(), "red dashed",
                "thick", "thin", "!length", "medium");

        assertAlternatives(p.getGrammar(), "red dashed 20px");
    }

    public void testTheBackgroundCase() {
        PropertyModel p = CssModuleSupport.getPropertyModel("background");
        assertResolve(p.getGrammar(), "aliceblue");
        assertAlternatives(p.getGrammar(), "aliceblue",
                "repeating-linear-gradient", "padding-box", "content-box", "round",
                "repeat", "!length", "repeating-radial-gradient", "space", "!percentage",
                "fixed", "border-box", "center", "no-repeat", "none", "left", "right",
                "top", "element", "scroll", "repeat-y", "linear-gradient", "repeat-x",
                "image", "url", "cross-fade", "radial-gradient", "bottom", "local");
    }

    public void testTheBackgroundCase2() {
        PropertyModel p = CssModuleSupport.getPropertyModel("background");
        assertResolve(p.getGrammar(), "aliceblue bottom / auto");
        assertAlternatives(p.getGrammar(), "aliceblue bottom / auto",
                "repeating-linear-gradient", "element", "padding-box", "scroll", ""
                + "content-box", "repeat-y", "linear-gradient", "repeat-x", "image",
                "round", "!length", "repeat", "repeating-radial-gradient", "space",
                "fixed", "!percentage", "url", "border-box", "cross-fade",
                "radial-gradient", "no-repeat", "auto", "none", "local");


    }

    public void testTheBackgroundCaseSimplified() {
        String g = " [ a , ]* b";
        assertAlternatives(g, "", "a", "b");
        assertAlternatives(g, "a", ",");
        assertAlternatives(g, "a,", "b", "a");
        assertAlternatives(g, "a,a,", "b", "a");
        assertAlternatives(g, "b");
        assertAlternatives(g, "a,b");
        assertAlternatives(g, "a,a,b");
    }

    public void testTheBackgroundCaseSimplified2() {
        String g = " [ [ a || b ] , ]* [ a || c ]";
        assertAlternatives(g, "", "a", "b", "c");

        assertAlternatives(g, "a", ",", "b", "c");

        assertAlternatives(g, "a b ,", "a", "b", "c");

        assertAlternatives(g, "b", ",", "a");

        assertAlternatives(g, "c", "a");
    }

     //Bug 205893 - font-family completion issue
     public void testFontFamily() {
        PropertyModel p = CssModuleSupport.getPropertyModel("font-family");
//        assertResolve(p.getGrammar(), "fantasy");
        assertAlternatives(p.getGrammar(), "fantasy", ",");

        assertAlternatives(p.getGrammar(), "fantasy, ", 
                "fantasy","serif","sans-serif","monospace","cursive", "!string","!identifier");
        
        assertAlternatives(p.getGrammar(), "fantasy, monospace", 
                ",");

        assertAlternatives(p.getGrammar(), "fantasy, monospace, ", 
                "fantasy","serif","sans-serif","monospace","cursive", "!string","!identifier");

    }
     
     public void testAnimation() {
        PropertyModel p = CssModuleSupport.getPropertyModel("animation");
//        assertResolve(p.getGrammar(), "fantasy");
        assertAlternatives(p.getGrammar(), "cubic-bezier",
                "alternate","linear","cubic-bezier","normal","ease","(","!time","ease-in",",","ease-in-out","ease-out","infinite","!number");

        
    }
    
    public void testPerformance_Parse_Resolve() {
//        Last results from my MacBook Pro:
//        -------------------------------------------
//        Testing parsing a grammar and resolving an input by the grammar...
//        50 iterations took 516 ms.
//        50 iterations took 297 ms.
//        50 iterations took 170 ms.
//        Average time of 50 iterations is 327
//        Average run of one resolve is 6 ms
        
        PropertyModel p = CssModuleSupport.getPropertyModel("background");

        //dry run
        assertResolve(p.getGrammar(), "aliceblue bottom / auto");

        System.out.println("Testing parsing a grammar and resolving an input by the grammar...");
        
        int loops = 3;
        int iterations = 50;
        int sum = 0;

        for (int l = 0; l < loops; l++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                assertResolve(p.getGrammar(), "aliceblue bottom / auto");
            }
            long stop = System.currentTimeMillis();

            System.out.println(String.format("%s iterations took %s ms.", iterations, (stop - start)));
            
            sum += (stop-start);
        }
        
        System.out.println(String.format("Average time of %s iterations is %s", iterations, (sum/loops)));
        System.out.println(String.format("Average run of one resolve is %s ms", (sum/loops/iterations)));
    }
    
    public void testPerformance_Resolve() {
//        Last results from my MacBook Pro:
//        -------------------------------------------
//        Testing parsing a grammar and resolving an input by the grammar...
//        50 iterations took 188 ms.
//        50 iterations took 121 ms.
//        50 iterations took 87 ms.
//        Average time of 50 iterations is 132
//        Average run of one resolve is 2 ms
        
        
        PropertyModel p = CssModuleSupport.getPropertyModel("background");

        //dry run
        assertResolve(p.getGrammarElement(), "aliceblue bottom / auto");

        System.out.println("Testing parsing a grammar and resolving an input by the grammar...");
        
        int loops = 3;
        int iterations = 50;
        int sum = 0;

        for (int l = 0; l < loops; l++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                assertResolve(p.getGrammarElement(), "aliceblue bottom / auto");
            }
            long stop = System.currentTimeMillis();

            System.out.println(String.format("%s iterations took %s ms.", iterations, (stop - start)));
            
            sum += (stop-start);
        }
        
        System.out.println(String.format("Average time of %s iterations is %s", iterations, (sum/loops)));
        System.out.println(String.format("Average run of one resolve is %s ms", (sum/loops/iterations)));
    }
    
    
}
