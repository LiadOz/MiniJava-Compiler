declare i32 @printf(i8*, ...)

@_cint = constant [4 x i8] c"%d\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}


define i32 @main() { 
; entry: --- requires starting from %0. if the label is not present, it is also assigned a name
  ; error: %0 = add i32 1, 1
  %1 = add i32 1, 1
  %2 = add i32 %1, %1 ; error to use %3 = add i32 %1, %1
  %result = add i32 %2, 1

  ; Call puts function to write out the string to stdout.
  call void @print_int(i32 %result)
  ret i32 0
}

; named temporaries are created when the result of a computation is not assigned to a named value.
; Unnamed temporaries are numbered sequentially (using a per-function incrementing counter, starting with 0). 
; Note that basic blocks and unnamed function parameters are included in this numbering. For example, if the entry basic block is not given a label name and all function parameters are named, then it will get number 0.