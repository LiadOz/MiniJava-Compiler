#!/bin/bash
echo BinarySearch
java -jar mjavac.jar unmarshal semantic tests/BinarySearch.java.xml out.res
diff out.res tests/ok.res
echo BubbleSort
java -jar mjavac.jar unmarshal semantic tests/BubbleSort.java.xml out.res
diff out.res tests/ok.res
echo LinearSearch
java -jar mjavac.jar unmarshal semantic tests/LinearSearch.java.xml out.res
diff out.res tests/ok.res
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
