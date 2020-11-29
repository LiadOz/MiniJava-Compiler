declare i32 @printf(i8*, ...)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define i32 @bar(i32 %a, i1 %b) {
  %_0 = and i32 %a, 15
  %_1 = add i1 %b, 1
  %_2 = and i1 %a, 1
  ret i32 %_0
}

define i32 @main() { 
  %result = call i32 @bar(i32 4, i1 1)
  call void @print_int(i32 %result)
  ret i32 0
}