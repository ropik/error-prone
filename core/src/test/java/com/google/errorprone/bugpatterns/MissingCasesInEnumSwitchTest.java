/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns;

import com.google.errorprone.CompilationTestHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** {@link MissingCasesInEnumSwitch}Test */
@RunWith(JUnit4.class)
public class MissingCasesInEnumSwitchTest {
  private CompilationTestHelper compilationHelper;

  @Before
  public void setUp() {
    compilationHelper = CompilationTestHelper.newInstance(new MissingCasesInEnumSwitch());
  }

  @Test
  public void testExhaustive() throws Exception {
    compilationHelper.assertCompileSucceeds(
        compilationHelper.fileManager().forSourceLines("Test.java",
            "class Test {",
            "  enum Case { ONE, TWO, THREE }",
            "  void m(Case c) {",
            "    switch (c) {",
            "      case ONE:",
            "      case TWO:",
            "      case THREE:",
            "        System.err.println(\"found it!\");",
            "        break;",
            "    }",
            "  }",
            "}"
        )
    );
  }

  @Test
  public void testNonExhaustive_withDefault() throws Exception {
    compilationHelper.assertCompileSucceeds(
        compilationHelper.fileManager().forSourceLines("Test.java",
            "class Test {",
            "  enum Case { ONE, TWO, THREE }",
            "  void m(Case c) {",
            "    switch (c) {",
            "      case ONE:",
            "      case TWO:",
            "        System.err.println(\"found it!\");",
            "        break;",
            "      default:",
            "        break;",
            "    }",
            "  }",
            "}"
        )
    );
  }

  @Test
  public void testNonExhaustive() throws Exception {
    compilationHelper.assertCompileFailsWithMessages(
        compilationHelper.fileManager().forSourceLines("Test.java",
            "class Test {",
            "  enum Case { ONE, TWO, THREE }",
            "  void m(Case c) {",
            "    // BUG: Diagnostic contains: Non-exhaustive switch, expected cases for: THREE",
            "    switch (c) {",
            "      case ONE:",
            "      case TWO:",
            "        System.err.println(\"found it!\");",
            "        break;",
            "    }",
            "  }",
            "}"
        )
    );
  }

  @Test
  public void testNonExhaustive_manyCases() throws Exception {
    compilationHelper.assertCompileFailsWithMessages(
        compilationHelper.fileManager().forSourceLines("Test.java",
            "class Test {",
            "  enum Case { ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT }",
            "  void m(Case c) {",
            "    // BUG: Diagnostic contains:",
            "    // Non-exhaustive switch, expected cases for: TWO, THREE, FOUR, and 4 others."
                + " Did you mean to include a 'default' case?",
            "    switch (c) {",
            "      case ONE:",
            "        System.err.println(\"found it!\");",
            "        break;",
            "    }",
            "  }",
            "}"
        )
    );
  }

  @Test
  public void testNonExhaustive_nonEnum() throws Exception {
    compilationHelper.assertCompileSucceeds(
        compilationHelper.fileManager().forSourceLines("Test.java",
            "class Test {",
            "  void m(int i) {",
            "    switch (i) {",
            "      case 1:",
            "      case 2:",
            "        System.err.println(\"found it!\");",
            "        break;",
            "    }",
            "  }",
            "}"
        )
    );
  }
}
