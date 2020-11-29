@.array = global [5 x i32] [i32 0, i32 1, i32 2, i32 3, i32 4]

declare i32 @printf(i8*, ...)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define i32 @bar(i32 %a, i32 %b) {
  %_1 = getelementptr [5 x i32], [5 x i32]* @.array, i32 0, i32 3
  ; %_0 = bitcast [5 x i32]* @.array to i32**
  ; %_1 = getelementptr i32*, i32** %_0, i32 0, i32 3 --- error
  %_2 = load i32, i32* %_1
  ret i32 %_2
}

define i32 @main() { 
  %result = call i32 @bar(i32 4, i32 2)
  call void @print_int(i32 %result)
  ret i32 0
}