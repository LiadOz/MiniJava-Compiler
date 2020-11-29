declare i32 @printf(i8*, ...)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define i32 @bar(i32 %a, i1 %b) {
  %p = alloca i32
  br i1 %b, label %then, label %else

then:
  %_0 = add i32 %a, 1
  store i32 %_0, i32* %p
  br label %join

else:
  %_1 = sub i32 %a, 1
  store i32 %_1, i32* %p
  br label %join

join:
  %res = load i32, i32* %p
  ret i32 %res
}

define i32 @main() { 
  %result = call i32 @bar(i32 4, i1 0)
  call void @print_int(i32 %result)
  ret i32 0
}