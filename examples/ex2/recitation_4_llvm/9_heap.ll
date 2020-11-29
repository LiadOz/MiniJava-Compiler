declare i32 @printf(i8*, ...)

declare i8* @calloc(i32, i32)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define i32 @bar(i32 %a, i1 %b) {
  %v = call i8* @calloc(i32 1, i32 8)
  %p = bitcast i8* %v to i32*
  store i32 %a, i32* %p
  %res = load i32, i32* %p
  ret i32 %res
}

define i32 @main() { 
  %result = call i32 @bar(i32 4, i1 1)
  call void @print_int(i32 %result)
  ret i32 0
}