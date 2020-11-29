declare i32 @printf(i8*, ...)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define i32 @double(i32 %x) {
	%_0 = mul i32 %x, 2
	ret i32 %_0
}

define i32 @bar(i32 %a, i32 %b) {
  %_0 = call i32 @double(i32 %a)
  %result = add i32 %_0, %b
  ret i32 %result
}

define i32 @main() { 
  %result = call i32 @bar(i32 4, i32 2)
  call void @print_int(i32 %result)
  ret i32 0
}