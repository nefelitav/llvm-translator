@.Main_vtable = global [0 x i8*] []

@.ArrayTest_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @ArrayTest.test to i8*)
]

@.B_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @B.test to i8*)
]

declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
@_cNSZ = constant [15 x i8] c"Negative size\0a\00"

define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define void @throw_oob() {
	%_str = bitcast [15 x i8]* @_cOOB to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}

define void @throw_nsz() {
	%_str = bitcast [15 x i8]* @_cNSZ to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}
define i32 @main() {
	%ab = alloca i8*
	%_0 = call i8* @calloc(i32 1, i32 20)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.ArrayTest_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	store i8* %_0, i8** %ab
	%_3 = load i8*, i8** %ab
	%_4 = bitcast i8* %_3 to i8***
	%_5 = load i8**, i8*** %_4
	%_6 = getelementptr i8*, i8** %_5, i32 0
	%_7 = load i8*, i8** %_6
	%_8 = bitcast i8* %_7 to i32 (i8* , i32)*
	%_9 = call i32 %_8(i8* %_3 , i32 3)
	call void (i32) @print_int(i32 %_9)
	ret i32 0

}

define i32 @ArrayTest.test(i8* %this, i32 %.num) {
	%num = alloca i32
	store i32 %.num, i32* %num
	%i = alloca i32
	%intArray = alloca i32*
	%_0 = load i32, i32* %num
	%_1 = add i32 1, %_0
	%_2 = icmp sge i32 %_1, 1
	br i1 %_2, label %nsz_ok_0, label %nsz_err_0

	nsz_err_0:
	call void @throw_nsz()
	br label %nsz_ok_0

	nsz_ok_0:
	%_3 = call i8* @calloc(i32 %_1, i32 4)
	%_4 = bitcast i8* %_3 to i32*
	store i32 %_0, i32* %_4
	store i32* %_4, i32** %intArray

	%_5 = getelementptr i8, i8* %this, i32 16
	%_6 = bitcast i8* %_5 to i32*
	store i32 0, i32* %_6

	%_7 = getelementptr i8, i8* %this, i32 16
	%_8 = bitcast i8* %_7 to i32*
	%_9 = load i32, i32* %_8
	call void (i32) @print_int(i32 %_9)
	%_10 = load i32*, i32** %intArray
	%_11 = load i32, i32* %_10
	call void (i32) @print_int(i32 %_11)
	store i32 0, i32* %i
	call void (i32) @print_int(i32 111)
	br label %loopstart1

	loopstart1:
	%_13 = load i32*, i32** %intArray
	%_14 = load i32, i32* %_13
	%_15 = load i32, i32* %i
	%_16 = icmp slt i32 %_15, %_14
	br i1 %_16, label %next1, label %end1

	next1:
	%_17 = load i32, i32* %i
	%_18 = add i32 %_17, 1
	call void (i32) @print_int(i32 %_18)
	%_19 = load i32, i32* %i
	%_20 = add i32 %_19, 1
	%_21 = load i32*, i32** %intArray
	%_22 = load i32, i32* %i
	%_23 = load i32, i32* %_21
	%_24 = icmp sge i32 %_22, 0
	%_25 = icmp slt i32 %_22, %_23
	%_26 = and i1 %_24, %_25
	br i1 %_26, label %oob_ok_2, label %oob_err_2

	oob_err_2:
	call void @throw_oob()
	br label %oob_ok_2

	oob_ok_2:
	%_27 = add i32 1, %_22
	%_28 = getelementptr i32, i32* %_21, i32 %_27
	store i32 %_20, i32* %_28
	%_29 = load i32, i32* %i
	%_30 = add i32 %_29, 1
	store i32 %_30, i32* %i
	br label %loopstart1


	end1:
	call void (i32) @print_int(i32 222)
	store i32 0, i32* %i
	br label %loopstart4

	loopstart4:
	%_31 = load i32*, i32** %intArray
	%_32 = load i32, i32* %_31
	%_33 = load i32, i32* %i
	%_34 = icmp slt i32 %_33, %_32
	br i1 %_34, label %next4, label %end4

	next4:
	%_35 = load i32*, i32** %intArray
	%_36 = load i32, i32* %i
	%_37 = load i32, i32* %_35
	%_38 = icmp sge i32 %_36, 0
	%_39 = icmp slt i32 %_36, %_37
	%_40 = and i1 %_38, %_39
	br i1 %_40, label %oob_ok_5, label %oob_err_5

	oob_err_5:
	call void @throw_oob()
	br label %oob_ok_5

	oob_ok_5:
	%_41 = add i32 1, %_36
	%_42 = getelementptr i32, i32* %_35, i32 %_41
	%_43 = load i32, i32* %_42
	call void (i32) @print_int(i32 %_43)
	%_45 = load i32, i32* %i
	%_46 = add i32 %_45, 1
	store i32 %_46, i32* %i
	br label %loopstart4


	end4:
	call void (i32) @print_int(i32 333)
	%_47 = load i32*, i32** %intArray
	%_48 = load i32, i32* %_47


	ret i32 %_48

}

define i32 @B.test(i8* %this, i32 %.num) {
	%num = alloca i32
	store i32 %.num, i32* %num
	%i = alloca i32
	%intArray = alloca i32*
	%_0 = load i32, i32* %num
	%_1 = add i32 1, %_0
	%_2 = icmp sge i32 %_1, 1
	br i1 %_2, label %nsz_ok_7, label %nsz_err_7

	nsz_err_7:
	call void @throw_nsz()
	br label %nsz_ok_7

	nsz_ok_7:
	%_3 = call i8* @calloc(i32 %_1, i32 4)
	%_4 = bitcast i8* %_3 to i32*
	store i32 %_0, i32* %_4
	store i32* %_4, i32** %intArray

	%_5 = getelementptr i8, i8* %this, i32 20
	%_6 = bitcast i8* %_5 to i32*
	store i32 12, i32* %_6

	%_7 = getelementptr i8, i8* %this, i32 20
	%_8 = bitcast i8* %_7 to i32*
	%_9 = load i32, i32* %_8
	call void (i32) @print_int(i32 %_9)
	%_10 = load i32*, i32** %intArray
	%_11 = load i32, i32* %_10
	call void (i32) @print_int(i32 %_11)
	store i32 0, i32* %i
	call void (i32) @print_int(i32 111)
	br label %loopstart8

	loopstart8:
	%_13 = load i32*, i32** %intArray
	%_14 = load i32, i32* %_13
	%_15 = load i32, i32* %i
	%_16 = icmp slt i32 %_15, %_14
	br i1 %_16, label %next8, label %end8

	next8:
	%_17 = load i32, i32* %i
	%_18 = add i32 %_17, 1
	call void (i32) @print_int(i32 %_18)
	%_19 = load i32, i32* %i
	%_20 = add i32 %_19, 1
	%_21 = load i32*, i32** %intArray
	%_22 = load i32, i32* %i
	%_23 = load i32, i32* %_21
	%_24 = icmp sge i32 %_22, 0
	%_25 = icmp slt i32 %_22, %_23
	%_26 = and i1 %_24, %_25
	br i1 %_26, label %oob_ok_9, label %oob_err_9

	oob_err_9:
	call void @throw_oob()
	br label %oob_ok_9

	oob_ok_9:
	%_27 = add i32 1, %_22
	%_28 = getelementptr i32, i32* %_21, i32 %_27
	store i32 %_20, i32* %_28
	%_29 = load i32, i32* %i
	%_30 = add i32 %_29, 1
	store i32 %_30, i32* %i
	br label %loopstart8


	end8:
	call void (i32) @print_int(i32 222)
	store i32 0, i32* %i
	br label %loopstart11

	loopstart11:
	%_31 = load i32*, i32** %intArray
	%_32 = load i32, i32* %_31
	%_33 = load i32, i32* %i
	%_34 = icmp slt i32 %_33, %_32
	br i1 %_34, label %next11, label %end11

	next11:
	%_35 = load i32*, i32** %intArray
	%_36 = load i32, i32* %i
	%_37 = load i32, i32* %_35
	%_38 = icmp sge i32 %_36, 0
	%_39 = icmp slt i32 %_36, %_37
	%_40 = and i1 %_38, %_39
	br i1 %_40, label %oob_ok_12, label %oob_err_12

	oob_err_12:
	call void @throw_oob()
	br label %oob_ok_12

	oob_ok_12:
	%_41 = add i32 1, %_36
	%_42 = getelementptr i32, i32* %_35, i32 %_41
	%_43 = load i32, i32* %_42
	call void (i32) @print_int(i32 %_43)
	%_45 = load i32, i32* %i
	%_46 = add i32 %_45, 1
	store i32 %_46, i32* %i
	br label %loopstart11


	end11:
	call void (i32) @print_int(i32 333)
	%_47 = load i32*, i32** %intArray
	%_48 = load i32, i32* %_47


	ret i32 %_48

}

