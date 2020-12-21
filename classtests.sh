#!/bin/bash
echo "#######################CLASS TESTS#########################"
echo GoodOverriding
java -jar mjavac.jar unmarshal semantic tests/GoodOverriding.xml out.res
diff out.res tests/ok.res
echo BadClassName
java -jar mjavac.jar unmarshal semantic tests/BadClassName.xml out.res
diff out.res tests/fail.res
echo BadClassOrder
java -jar mjavac.jar unmarshal semantic tests/BadClassOrder.xml out.res
diff out.res tests/fail.res
echo BadOverriding
java -jar mjavac.jar unmarshal semantic tests/BadOverriding.xml out.res
diff out.res tests/fail.res
echo BadOverriding2
java -jar mjavac.jar unmarshal semantic tests/BadOverriding2.xml out.res
diff out.res tests/fail.res
echo DuplicateClass
java -jar mjavac.jar unmarshal semantic tests/DuplicateClass.xml out.res
diff out.res tests/fail.res
echo DuplicateField
java -jar mjavac.jar unmarshal semantic tests/DuplicateField.xml out.res
diff out.res tests/fail.res
echo DuplicateMethod
java -jar mjavac.jar unmarshal semantic tests/DuplicateMethod.xml out.res
diff out.res tests/fail.res
echo DuplicateVarName
java -jar mjavac.jar unmarshal semantic tests/DuplicateVarName.xml out.res
diff out.res tests/fail.res
echo ExtendMainClass
java -jar mjavac.jar unmarshal semantic tests/ExtendMainClass.xml out.res
diff out.res tests/fail.res
echo ExtendSelf
java -jar mjavac.jar unmarshal semantic tests/ExtendSelf.xml out.res
diff out.res tests/fail.res
echo SubclassDuplicateField
java -jar mjavac.jar unmarshal semantic tests/SubclassDuplicateField.xml out.res
diff out.res tests/fail.res
echo SubclassDuplicateMethod
java -jar mjavac.jar unmarshal semantic tests/SubclassDuplicateMethod.xml out.res
diff out.res tests/fail.res
echo BadCaller
java -jar mjavac.jar unmarshal semantic tests/BadCaller.xml out.res
diff out.res tests/fail.res
echo ComplexCaller
java -jar mjavac.jar unmarshal semantic tests/ComplexCaller.xml out.res
diff out.res tests/fail.res
echo BinarySearch
java -jar mjavac.jar unmarshal semantic examples/ast/BinarySearch.java.xml out.res
diff out.res tests/ok.res
echo BinaryTree
java -jar mjavac.jar unmarshal semantic examples/ast/BinaryTree.java.xml out.res
diff out.res tests/ok.res
echo BubbleSort
java -jar mjavac.jar unmarshal semantic examples/ast/BubbleSort.java.xml out.res
diff out.res tests/ok.res
echo Factorial
java -jar mjavac.jar unmarshal semantic examples/ast/Factorial.java.xml out.res
diff out.res tests/ok.res
echo LinearSearch
java -jar mjavac.jar unmarshal semantic examples/ast/LinearSearch.java.xml out.res
diff out.res tests/ok.res
echo LinkedList
java -jar mjavac.jar unmarshal semantic examples/ast/LinkedList.java.xml out.res
diff out.res tests/ok.res
echo QuickSort
java -jar mjavac.jar unmarshal semantic examples/ast/QuickSort.java.xml out.res
diff out.res tests/ok.res
echo TreeVisitor
java -jar mjavac.jar unmarshal semantic examples/ast/TreeVisitor.java.xml out.res
diff out.res tests/ok.res

echo "#######################TYPE TESTS#########################"

echo InvalidArrayAccess
java -jar mjavac.jar unmarshal semantic tests/InvalidArrayAccess.java.xml out.res
diff out.res tests/fail.res

echo InvalidLessThan
java -jar mjavac.jar unmarshal semantic tests/InvalidLessThan.java.xml out.res
diff out.res tests/fail.res

echo InvalidIfCondition
java -jar mjavac.jar unmarshal semantic tests/InvalidIfCondition.java.xml out.res
diff out.res tests/fail.res

echo InvalidSysout
java -jar mjavac.jar unmarshal semantic tests/InvalidSysout.java.xml out.res
diff out.res tests/fail.res

echo InvalidArgumentType
java -jar mjavac.jar unmarshal semantic tests/InvalidArgumentType.java.xml out.res
diff out.res tests/fail.res

echo InvalidArrayLength
java -jar mjavac.jar unmarshal semantic tests/InvalidArrayLength.java.xml out.res
diff out.res tests/fail.res
