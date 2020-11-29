declare i32 @printf(i8*, ...)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define i32 @factorial(i32 %a) {
  %_0 = icmp eq i32 %a, 0
  br i1 %_0, label %then, label %else

then:
  ret i32 1

else:
  %_1 = sub i32 %a, 1
  %_2 = call i32 @factorial(i32 %_1)
  %_3 = mul i32 %_2, %a
  ret i32 %_3
}

define i32 @main() { 
  %result = call i32 @factorial(i32 4)
  call void @print_int(i32 %result)
  ret i32 0
}