@.LinkedList_vtable = global [0 x i8*] []

@.Element_vtable = global [6 x i8*] [
	i8* bitcast (i1 (i8*, i32, i32, i1)* @Element.Init to i8*),
	i8* bitcast (i32 (i8*)* @Element.GetAge to i8*),
	i8* bitcast (i32 (i8*)* @Element.GetSalary to i8*),
	i8* bitcast (i1 (i8*)* @Element.GetMarried to i8*),
	i8* bitcast (i1 (i8*, i8*)* @Element.Equal to i8*),
	i8* bitcast (i1 (i8*, i32, i32)* @Element.Compare to i8*)
]

@.List_vtable = global [10 x i8*] [
	i8* bitcast (i1 (i8*)* @List.Init to i8*),
	i8* bitcast (i1 (i8*, i8*, i8*, i1)* @List.InitNew to i8*),
	i8* bitcast (i8* (i8*, i8*)* @List.Insert to i8*),
	i8* bitcast (i1 (i8*, i8*)* @List.SetNext to i8*),
	i8* bitcast (i8* (i8*, i8*)* @List.Delete to i8*),
	i8* bitcast (i32 (i8*, i8*)* @List.Search to i8*),
	i8* bitcast (i1 (i8*)* @List.GetEnd to i8*),
	i8* bitcast (i8* (i8*)* @List.GetElem to i8*),
	i8* bitcast (i8* (i8*)* @List.GetNext to i8*),
	i8* bitcast (i1 (i8*)* @List.Print to i8*)
]

@.LL_vtable = global [1 x i8*] [
	i8* bitcast (i32 (i8*)* @LL.Start to i8*)
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
	%_0 = call i8* @calloc(i32 1, i32 8)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.LL_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32 (i8* )*
	%_8 = call i32 %_7(i8* %_0 )
	call void (i32) @print_int(i32 %_8)
	ret i32 0

}

define i1 @Element.Init(i8* %this, i32 %.v_Age,i32 %.v_Salary,i1 %.v_Married) {
	%v_Age = alloca i32
	store i32 %.v_Age, i32* %v_Age
	%v_Salary = alloca i32
	store i32 %.v_Salary, i32* %v_Salary
	%v_Married = alloca i1
	store i1 %.v_Married, i1* %v_Married
	%_0 = load i32, i32* %v_Age

	%_1 = getelementptr i8, i8* %this, i32 8
	%_2 = bitcast i8* %_1 to i32*
	store i32 %_0, i32* %_2
	%_3 = load i32, i32* %v_Salary

	%_4 = getelementptr i8, i8* %this, i32 12
	%_5 = bitcast i8* %_4 to i32*
	store i32 %_3, i32* %_5
	%_6 = load i1, i1* %v_Married

	%_7 = getelementptr i8, i8* %this, i32 16
	%_8 = bitcast i8* %_7 to i1*
	store i1 %_6, i1* %_8


	ret i1 1

}

define i32 @Element.GetAge(i8* %this) {


	%_0 = getelementptr i8, i8* %this, i32 8
	%_1 = bitcast i8* %_0 to i32*
	%_2 = load i32, i32* %_1

	ret i32 %_2

}

define i32 @Element.GetSalary(i8* %this) {


	%_0 = getelementptr i8, i8* %this, i32 12
	%_1 = bitcast i8* %_0 to i32*
	%_2 = load i32, i32* %_1

	ret i32 %_2

}

define i1 @Element.GetMarried(i8* %this) {


	%_0 = getelementptr i8, i8* %this, i32 16
	%_1 = bitcast i8* %_0 to i1*
	%_2 = load i1, i1* %_1

	ret i1 %_2

}

define i1 @Element.Equal(i8* %this, i8* %.other) {
	%other = alloca i8*
	store i8* %.other, i8** %other
	%ret_val = alloca i1
	%aux01 = alloca i32
	%aux02 = alloca i32
	%nt = alloca i32
	store i1 1, i1* %ret_val
	%_0 = load i8*, i8** %other
	%_1 = bitcast i8* %_0 to i8***
	%_2 = load i8**, i8*** %_1
	%_3 = getelementptr i8*, i8** %_2, i32 1
	%_4 = load i8*, i8** %_3
	%_5 = bitcast i8* %_4 to i32 (i8* )*
	%_6 = call i32 %_5(i8* %_0 )
	store i32 %_6, i32* %aux01
	%_7 = bitcast i8* %this to i8***
	%_8 = load i8**, i8*** %_7
	%_9 = getelementptr i8*, i8** %_8, i32 5
	%_10 = load i8*, i8** %_9
	%_11 = load i32, i32* %aux01

	%_12 = getelementptr i8, i8* %this, i32 8
	%_13 = bitcast i8* %_12 to i32*
	%_14 = load i32, i32* %_13
	%_15 = bitcast i8* %_10 to i1 (i8* , i32, i32)*
	%_16 = call i1 %_15(i8* %this , i32 %_11, i32 %_14)
	%_17 = xor i1 1, %_16
	br i1 %_17, label %if_then_0, label %if_else_0


	if_then_0:
	store i1 0, i1* %ret_val
	br label %if_end_0

	if_else_0:
	%_18 = load i8*, i8** %other
	%_19 = bitcast i8* %_18 to i8***
	%_20 = load i8**, i8*** %_19
	%_21 = getelementptr i8*, i8** %_20, i32 2
	%_22 = load i8*, i8** %_21
	%_23 = bitcast i8* %_22 to i32 (i8* )*
	%_24 = call i32 %_23(i8* %_18 )
	store i32 %_24, i32* %aux02
	%_25 = bitcast i8* %this to i8***
	%_26 = load i8**, i8*** %_25
	%_27 = getelementptr i8*, i8** %_26, i32 5
	%_28 = load i8*, i8** %_27
	%_29 = load i32, i32* %aux02

	%_30 = getelementptr i8, i8* %this, i32 12
	%_31 = bitcast i8* %_30 to i32*
	%_32 = load i32, i32* %_31
	%_33 = bitcast i8* %_28 to i1 (i8* , i32, i32)*
	%_34 = call i1 %_33(i8* %this , i32 %_29, i32 %_32)
	%_35 = xor i1 1, %_34
	br i1 %_35, label %if_then_1, label %if_else_1


	if_then_1:
	store i1 0, i1* %ret_val
	br label %if_end_1

	if_else_1:

	%_36 = getelementptr i8, i8* %this, i32 16
	%_37 = bitcast i8* %_36 to i1*
	%_38 = load i1, i1* %_37
	br i1 %_38, label %if_then_2, label %if_else_2


	if_then_2:
	%_39 = load i8*, i8** %other
	%_40 = bitcast i8* %_39 to i8***
	%_41 = load i8**, i8*** %_40
	%_42 = getelementptr i8*, i8** %_41, i32 3
	%_43 = load i8*, i8** %_42
	%_44 = bitcast i8* %_43 to i1 (i8* )*
	%_45 = call i1 %_44(i8* %_39 )
	%_46 = xor i1 1, %_45
	br i1 %_46, label %if_then_3, label %if_else_3


	if_then_3:
	store i1 0, i1* %ret_val
	br label %if_end_3

	if_else_3:
	store i32 0, i32* %nt
	br label %if_end_3

	if_end_3:
	br label %if_end_2

	if_else_2:
	%_47 = load i8*, i8** %other
	%_48 = bitcast i8* %_47 to i8***
	%_49 = load i8**, i8*** %_48
	%_50 = getelementptr i8*, i8** %_49, i32 3
	%_51 = load i8*, i8** %_50
	%_52 = bitcast i8* %_51 to i1 (i8* )*
	%_53 = call i1 %_52(i8* %_47 )
	br i1 %_53, label %if_then_5, label %if_else_5


	if_then_5:
	store i1 0, i1* %ret_val
	br label %if_end_5

	if_else_5:
	store i32 0, i32* %nt
	br label %if_end_5

	if_end_5:
	br label %if_end_2

	if_end_2:
	br label %if_end_1

	if_end_1:
	br label %if_end_0

	if_end_0:

	%_54 = load i1, i1* %ret_val

	ret i1 %_54

}

define i1 @Element.Compare(i8* %this, i32 %.num1,i32 %.num2) {
	%num1 = alloca i32
	store i32 %.num1, i32* %num1
	%num2 = alloca i32
	store i32 %.num2, i32* %num2
	%retval = alloca i1
	%aux02 = alloca i32
	store i1 0, i1* %retval
	%_0 = load i32, i32* %num2
	%_1 = add i32 %_0, 1
	store i32 %_1, i32* %aux02
	%_2 = load i32, i32* %num1
	%_3 = load i32, i32* %num2
	%_4 = icmp slt i32 %_2, %_3
	br i1 %_4, label %if_then_10, label %if_else_10


	if_then_10:
	store i1 0, i1* %retval
	br label %if_end_10

	if_else_10:
	%_5 = load i32, i32* %num1
	%_6 = load i32, i32* %aux02
	%_7 = icmp slt i32 %_5, %_6
	%_8 = xor i1 1, %_7
	br i1 %_8, label %if_then_11, label %if_else_11


	if_then_11:
	store i1 0, i1* %retval
	br label %if_end_11

	if_else_11:
	store i1 1, i1* %retval
	br label %if_end_11

	if_end_11:
	br label %if_end_10

	if_end_10:

	%_9 = load i1, i1* %retval

	ret i1 %_9

}

define i1 @List.Init(i8* %this) {

	%_0 = getelementptr i8, i8* %this, i32 24
	%_1 = bitcast i8* %_0 to i1*
	store i1 1, i1* %_1


	ret i1 1

}

define i1 @List.InitNew(i8* %this, i8* %.v_elem,i8* %.v_next,i1 %.v_end) {
	%v_elem = alloca i8*
	store i8* %.v_elem, i8** %v_elem
	%v_next = alloca i8*
	store i8* %.v_next, i8** %v_next
	%v_end = alloca i1
	store i1 %.v_end, i1* %v_end
	%_0 = load i1, i1* %v_end

	%_1 = getelementptr i8, i8* %this, i32 24
	%_2 = bitcast i8* %_1 to i1*
	store i1 %_0, i1* %_2
	%_3 = load i8*, i8** %v_elem

	%_4 = getelementptr i8, i8* %this, i32 8
	%_5 = bitcast i8* %_4 to i8**
	store i8* %_3, i8** %_5
	%_6 = load i8*, i8** %v_next

	%_7 = getelementptr i8, i8* %this, i32 16
	%_8 = bitcast i8* %_7 to i8**
	store i8* %_6, i8** %_8


	ret i1 1

}

define i8* @List.Insert(i8* %this, i8* %.new_elem) {
	%new_elem = alloca i8*
	store i8* %.new_elem, i8** %new_elem
	%ret_val = alloca i1
	%aux03 = alloca i8*
	%aux02 = alloca i8*
	store i8* %this, i8** %aux03
	%_0 = call i8* @calloc(i32 1, i32 25)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [10 x i8*], [10 x i8*]* @.List_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	store i8* %_0, i8** %aux02
	%_3 = load i8*, i8** %aux02
	%_4 = bitcast i8* %_3 to i8***
	%_5 = load i8**, i8*** %_4
	%_6 = getelementptr i8*, i8** %_5, i32 1
	%_7 = load i8*, i8** %_6
	%_8 = load i8*, i8** %new_elem
	%_9 = load i8*, i8** %aux03
	%_10 = bitcast i8* %_7 to i1 (i8* , i8*, i8*, i1)*
	%_11 = call i1 %_10(i8* %_3 , i8* %_8, i8* %_9, i1 0)
	store i1 %_11, i1* %ret_val

	%_12 = load i8*, i8** %aux02

	ret i8* %_12

}

define i1 @List.SetNext(i8* %this, i8* %.v_next) {
	%v_next = alloca i8*
	store i8* %.v_next, i8** %v_next
	%_0 = load i8*, i8** %v_next

	%_1 = getelementptr i8, i8* %this, i32 16
	%_2 = bitcast i8* %_1 to i8**
	store i8* %_0, i8** %_2


	ret i1 1

}

define i8* @List.Delete(i8* %this, i8* %.e) {
	%e = alloca i8*
	store i8* %.e, i8** %e
	%my_head = alloca i8*
	%ret_val = alloca i1
	%aux05 = alloca i1
	%aux01 = alloca i8*
	%prev = alloca i8*
	%var_end = alloca i1
	%var_elem = alloca i8*
	%aux04 = alloca i32
	%nt = alloca i32
	store i8* %this, i8** %my_head
	store i1 0, i1* %ret_val
	%_0 = sub i32 0, 1
	store i32 %_0, i32* %aux04
	store i8* %this, i8** %aux01
	store i8* %this, i8** %prev

	%_1 = getelementptr i8, i8* %this, i32 24
	%_2 = bitcast i8* %_1 to i1*
	%_3 = load i1, i1* %_2
	store i1 %_3, i1* %var_end

	%_4 = getelementptr i8, i8* %this, i32 8
	%_5 = bitcast i8* %_4 to i8**
	%_6 = load i8*, i8** %_5
	store i8* %_6, i8** %var_elem
	br label %loopstart14

	loopstart14:
	%_7 = load i1, i1* %var_end
	%_8 = xor i1 1, %_7
	%_9 = load i1, i1* %ret_val
	%_10 = xor i1 1, %_9
	br i1 %_8, label %exp_res_15, label %exp_res_14

	exp_res_14:
	br label %exp_res_17

	exp_res_15:
	br label %exp_res_16

	exp_res_16:
	br label %exp_res_17

	exp_res_17:
	%_11 = phi i1  [ 0, %exp_res_14 ], [ %_10, %exp_res_16 ]
	br i1 %_11, label %next14, label %end14

	next14:
	%_12 = load i8*, i8** %e
	%_13 = bitcast i8* %_12 to i8***
	%_14 = load i8**, i8*** %_13
	%_15 = getelementptr i8*, i8** %_14, i32 4
	%_16 = load i8*, i8** %_15
	%_17 = load i8*, i8** %var_elem
	%_18 = bitcast i8* %_16 to i1 (i8* , i8*)*
	%_19 = call i1 %_18(i8* %_12 , i8* %_17)
	br i1 %_19, label %if_then_19, label %if_else_19


	if_then_19:
	store i1 1, i1* %ret_val
	%_20 = load i32, i32* %aux04
	%_21 = icmp slt i32 %_20, 0
	br i1 %_21, label %if_then_20, label %if_else_20


	if_then_20:
	%_22 = load i8*, i8** %aux01
	%_23 = bitcast i8* %_22 to i8***
	%_24 = load i8**, i8*** %_23
	%_25 = getelementptr i8*, i8** %_24, i32 8
	%_26 = load i8*, i8** %_25
	%_27 = bitcast i8* %_26 to i8* (i8* )*
	%_28 = call i8* %_27(i8* %_22 )
	store i8* %_28, i8** %my_head
	br label %if_end_20

	if_else_20:
	%_29 = sub i32 0, 555
	call void (i32) @print_int(i32 %_29)
	%_30 = load i8*, i8** %prev
	%_31 = load i8*, i8** %aux01
	%_32 = bitcast i8* %_31 to i8***
	%_33 = load i8**, i8*** %_32
	%_34 = getelementptr i8*, i8** %_33, i32 8
	%_35 = load i8*, i8** %_34
	%_36 = bitcast i8* %_35 to i8* (i8* )*
	%_37 = call i8* %_36(i8* %_31 )
	%_38 = bitcast i8* %_30 to i8***
	%_39 = load i8**, i8*** %_38
	%_40 = getelementptr i8*, i8** %_39, i32 3
	%_41 = load i8*, i8** %_40
	%_42 = bitcast i8* %_41 to i1 (i8* , i8*)*
	%_43 = call i1 %_42(i8* %_30 , i8* %_37)
	store i1 %_43, i1* %aux05
	%_44 = sub i32 0, 555
	call void (i32) @print_int(i32 %_44)
	br label %if_end_20

	if_end_20:
	br label %if_end_19

	if_else_19:
	store i32 0, i32* %nt
	br label %if_end_19

	if_end_19:
	%_45 = load i1, i1* %ret_val
	%_46 = xor i1 1, %_45
	br i1 %_46, label %if_then_23, label %if_else_23


	if_then_23:
	%_47 = load i8*, i8** %aux01
	store i8* %_47, i8** %prev
	%_48 = load i8*, i8** %aux01
	%_49 = bitcast i8* %_48 to i8***
	%_50 = load i8**, i8*** %_49
	%_51 = getelementptr i8*, i8** %_50, i32 8
	%_52 = load i8*, i8** %_51
	%_53 = bitcast i8* %_52 to i8* (i8* )*
	%_54 = call i8* %_53(i8* %_48 )
	store i8* %_54, i8** %aux01
	%_55 = load i8*, i8** %aux01
	%_56 = bitcast i8* %_55 to i8***
	%_57 = load i8**, i8*** %_56
	%_58 = getelementptr i8*, i8** %_57, i32 6
	%_59 = load i8*, i8** %_58
	%_60 = bitcast i8* %_59 to i1 (i8* )*
	%_61 = call i1 %_60(i8* %_55 )
	store i1 %_61, i1* %var_end
	%_62 = load i8*, i8** %aux01
	%_63 = bitcast i8* %_62 to i8***
	%_64 = load i8**, i8*** %_63
	%_65 = getelementptr i8*, i8** %_64, i32 7
	%_66 = load i8*, i8** %_65
	%_67 = bitcast i8* %_66 to i8* (i8* )*
	%_68 = call i8* %_67(i8* %_62 )
	store i8* %_68, i8** %var_elem
	store i32 1, i32* %aux04
	br label %if_end_23

	if_else_23:
	store i32 0, i32* %nt
	br label %if_end_23

	if_end_23:
	br label %loopstart14


	end14:

	%_69 = load i8*, i8** %my_head

	ret i8* %_69

}

define i32 @List.Search(i8* %this, i8* %.e) {
	%e = alloca i8*
	store i8* %.e, i8** %e
	%int_ret_val = alloca i32
	%aux01 = alloca i8*
	%var_elem = alloca i8*
	%var_end = alloca i1
	%nt = alloca i32
	store i32 0, i32* %int_ret_val
	store i8* %this, i8** %aux01

	%_0 = getelementptr i8, i8* %this, i32 24
	%_1 = bitcast i8* %_0 to i1*
	%_2 = load i1, i1* %_1
	store i1 %_2, i1* %var_end

	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i8**
	%_5 = load i8*, i8** %_4
	store i8* %_5, i8** %var_elem
	br label %loopstart26

	loopstart26:
	%_6 = load i1, i1* %var_end
	%_7 = xor i1 1, %_6
	br i1 %_7, label %next26, label %end26

	next26:
	%_8 = load i8*, i8** %e
	%_9 = bitcast i8* %_8 to i8***
	%_10 = load i8**, i8*** %_9
	%_11 = getelementptr i8*, i8** %_10, i32 4
	%_12 = load i8*, i8** %_11
	%_13 = load i8*, i8** %var_elem
	%_14 = bitcast i8* %_12 to i1 (i8* , i8*)*
	%_15 = call i1 %_14(i8* %_8 , i8* %_13)
	br i1 %_15, label %if_then_27, label %if_else_27


	if_then_27:
	store i32 1, i32* %int_ret_val
	br label %if_end_27

	if_else_27:
	store i32 0, i32* %nt
	br label %if_end_27

	if_end_27:
	%_16 = load i8*, i8** %aux01
	%_17 = bitcast i8* %_16 to i8***
	%_18 = load i8**, i8*** %_17
	%_19 = getelementptr i8*, i8** %_18, i32 8
	%_20 = load i8*, i8** %_19
	%_21 = bitcast i8* %_20 to i8* (i8* )*
	%_22 = call i8* %_21(i8* %_16 )
	store i8* %_22, i8** %aux01
	%_23 = load i8*, i8** %aux01
	%_24 = bitcast i8* %_23 to i8***
	%_25 = load i8**, i8*** %_24
	%_26 = getelementptr i8*, i8** %_25, i32 6
	%_27 = load i8*, i8** %_26
	%_28 = bitcast i8* %_27 to i1 (i8* )*
	%_29 = call i1 %_28(i8* %_23 )
	store i1 %_29, i1* %var_end
	%_30 = load i8*, i8** %aux01
	%_31 = bitcast i8* %_30 to i8***
	%_32 = load i8**, i8*** %_31
	%_33 = getelementptr i8*, i8** %_32, i32 7
	%_34 = load i8*, i8** %_33
	%_35 = bitcast i8* %_34 to i8* (i8* )*
	%_36 = call i8* %_35(i8* %_30 )
	store i8* %_36, i8** %var_elem
	br label %loopstart26


	end26:

	%_37 = load i32, i32* %int_ret_val

	ret i32 %_37

}

define i1 @List.GetEnd(i8* %this) {


	%_0 = getelementptr i8, i8* %this, i32 24
	%_1 = bitcast i8* %_0 to i1*
	%_2 = load i1, i1* %_1

	ret i1 %_2

}

define i8* @List.GetElem(i8* %this) {


	%_0 = getelementptr i8, i8* %this, i32 8
	%_1 = bitcast i8* %_0 to i8**
	%_2 = load i8*, i8** %_1

	ret i8* %_2

}

define i8* @List.GetNext(i8* %this) {


	%_0 = getelementptr i8, i8* %this, i32 16
	%_1 = bitcast i8* %_0 to i8**
	%_2 = load i8*, i8** %_1

	ret i8* %_2

}

define i1 @List.Print(i8* %this) {
	%aux01 = alloca i8*
	%var_end = alloca i1
	%var_elem = alloca i8*
	store i8* %this, i8** %aux01

	%_0 = getelementptr i8, i8* %this, i32 24
	%_1 = bitcast i8* %_0 to i1*
	%_2 = load i1, i1* %_1
	store i1 %_2, i1* %var_end

	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i8**
	%_5 = load i8*, i8** %_4
	store i8* %_5, i8** %var_elem
	br label %loopstart30

	loopstart30:
	%_6 = load i1, i1* %var_end
	%_7 = xor i1 1, %_6
	br i1 %_7, label %next30, label %end30

	next30:
	%_8 = load i8*, i8** %var_elem
	%_9 = bitcast i8* %_8 to i8***
	%_10 = load i8**, i8*** %_9
	%_11 = getelementptr i8*, i8** %_10, i32 1
	%_12 = load i8*, i8** %_11
	%_13 = bitcast i8* %_12 to i32 (i8* )*
	%_14 = call i32 %_13(i8* %_8 )
	call void (i32) @print_int(i32 %_14)
	%_15 = load i8*, i8** %aux01
	%_16 = bitcast i8* %_15 to i8***
	%_17 = load i8**, i8*** %_16
	%_18 = getelementptr i8*, i8** %_17, i32 8
	%_19 = load i8*, i8** %_18
	%_20 = bitcast i8* %_19 to i8* (i8* )*
	%_21 = call i8* %_20(i8* %_15 )
	store i8* %_21, i8** %aux01
	%_22 = load i8*, i8** %aux01
	%_23 = bitcast i8* %_22 to i8***
	%_24 = load i8**, i8*** %_23
	%_25 = getelementptr i8*, i8** %_24, i32 6
	%_26 = load i8*, i8** %_25
	%_27 = bitcast i8* %_26 to i1 (i8* )*
	%_28 = call i1 %_27(i8* %_22 )
	store i1 %_28, i1* %var_end
	%_29 = load i8*, i8** %aux01
	%_30 = bitcast i8* %_29 to i8***
	%_31 = load i8**, i8*** %_30
	%_32 = getelementptr i8*, i8** %_31, i32 7
	%_33 = load i8*, i8** %_32
	%_34 = bitcast i8* %_33 to i8* (i8* )*
	%_35 = call i8* %_34(i8* %_29 )
	store i8* %_35, i8** %var_elem
	br label %loopstart30


	end30:


	ret i1 1

}

define i32 @LL.Start(i8* %this) {
	%head = alloca i8*
	%last_elem = alloca i8*
	%aux01 = alloca i1
	%el01 = alloca i8*
	%el02 = alloca i8*
	%el03 = alloca i8*
	%_0 = call i8* @calloc(i32 1, i32 25)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [10 x i8*], [10 x i8*]* @.List_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	store i8* %_0, i8** %last_elem
	%_3 = load i8*, i8** %last_elem
	%_4 = bitcast i8* %_3 to i8***
	%_5 = load i8**, i8*** %_4
	%_6 = getelementptr i8*, i8** %_5, i32 0
	%_7 = load i8*, i8** %_6
	%_8 = bitcast i8* %_7 to i1 (i8* )*
	%_9 = call i1 %_8(i8* %_3 )
	store i1 %_9, i1* %aux01
	%_10 = load i8*, i8** %last_elem
	store i8* %_10, i8** %head
	%_11 = load i8*, i8** %head
	%_12 = bitcast i8* %_11 to i8***
	%_13 = load i8**, i8*** %_12
	%_14 = getelementptr i8*, i8** %_13, i32 0
	%_15 = load i8*, i8** %_14
	%_16 = bitcast i8* %_15 to i1 (i8* )*
	%_17 = call i1 %_16(i8* %_11 )
	store i1 %_17, i1* %aux01
	%_18 = load i8*, i8** %head
	%_19 = bitcast i8* %_18 to i8***
	%_20 = load i8**, i8*** %_19
	%_21 = getelementptr i8*, i8** %_20, i32 9
	%_22 = load i8*, i8** %_21
	%_23 = bitcast i8* %_22 to i1 (i8* )*
	%_24 = call i1 %_23(i8* %_18 )
	store i1 %_24, i1* %aux01
	%_25 = call i8* @calloc(i32 1, i32 17)
	%_26 = bitcast i8* %_25 to i8***
	%_27 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_27, i8*** %_26
	store i8* %_25, i8** %el01
	%_28 = load i8*, i8** %el01
	%_29 = bitcast i8* %_28 to i8***
	%_30 = load i8**, i8*** %_29
	%_31 = getelementptr i8*, i8** %_30, i32 0
	%_32 = load i8*, i8** %_31
	%_33 = bitcast i8* %_32 to i1 (i8* , i32, i32, i1)*
	%_34 = call i1 %_33(i8* %_28 , i32 25, i32 37000, i1 0)
	store i1 %_34, i1* %aux01
	%_35 = load i8*, i8** %head
	%_36 = bitcast i8* %_35 to i8***
	%_37 = load i8**, i8*** %_36
	%_38 = getelementptr i8*, i8** %_37, i32 2
	%_39 = load i8*, i8** %_38
	%_40 = load i8*, i8** %el01
	%_41 = bitcast i8* %_39 to i8* (i8* , i8*)*
	%_42 = call i8* %_41(i8* %_35 , i8* %_40)
	store i8* %_42, i8** %head
	%_43 = load i8*, i8** %head
	%_44 = bitcast i8* %_43 to i8***
	%_45 = load i8**, i8*** %_44
	%_46 = getelementptr i8*, i8** %_45, i32 9
	%_47 = load i8*, i8** %_46
	%_48 = bitcast i8* %_47 to i1 (i8* )*
	%_49 = call i1 %_48(i8* %_43 )
	store i1 %_49, i1* %aux01
	call void (i32) @print_int(i32 10000000)
	%_50 = call i8* @calloc(i32 1, i32 17)
	%_51 = bitcast i8* %_50 to i8***
	%_52 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_52, i8*** %_51
	store i8* %_50, i8** %el01
	%_53 = load i8*, i8** %el01
	%_54 = bitcast i8* %_53 to i8***
	%_55 = load i8**, i8*** %_54
	%_56 = getelementptr i8*, i8** %_55, i32 0
	%_57 = load i8*, i8** %_56
	%_58 = bitcast i8* %_57 to i1 (i8* , i32, i32, i1)*
	%_59 = call i1 %_58(i8* %_53 , i32 39, i32 42000, i1 1)
	store i1 %_59, i1* %aux01
	%_60 = load i8*, i8** %el01
	store i8* %_60, i8** %el02
	%_61 = load i8*, i8** %head
	%_62 = bitcast i8* %_61 to i8***
	%_63 = load i8**, i8*** %_62
	%_64 = getelementptr i8*, i8** %_63, i32 2
	%_65 = load i8*, i8** %_64
	%_66 = load i8*, i8** %el01
	%_67 = bitcast i8* %_65 to i8* (i8* , i8*)*
	%_68 = call i8* %_67(i8* %_61 , i8* %_66)
	store i8* %_68, i8** %head
	%_69 = load i8*, i8** %head
	%_70 = bitcast i8* %_69 to i8***
	%_71 = load i8**, i8*** %_70
	%_72 = getelementptr i8*, i8** %_71, i32 9
	%_73 = load i8*, i8** %_72
	%_74 = bitcast i8* %_73 to i1 (i8* )*
	%_75 = call i1 %_74(i8* %_69 )
	store i1 %_75, i1* %aux01
	call void (i32) @print_int(i32 10000000)
	%_76 = call i8* @calloc(i32 1, i32 17)
	%_77 = bitcast i8* %_76 to i8***
	%_78 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_78, i8*** %_77
	store i8* %_76, i8** %el01
	%_79 = load i8*, i8** %el01
	%_80 = bitcast i8* %_79 to i8***
	%_81 = load i8**, i8*** %_80
	%_82 = getelementptr i8*, i8** %_81, i32 0
	%_83 = load i8*, i8** %_82
	%_84 = bitcast i8* %_83 to i1 (i8* , i32, i32, i1)*
	%_85 = call i1 %_84(i8* %_79 , i32 22, i32 34000, i1 0)
	store i1 %_85, i1* %aux01
	%_86 = load i8*, i8** %head
	%_87 = bitcast i8* %_86 to i8***
	%_88 = load i8**, i8*** %_87
	%_89 = getelementptr i8*, i8** %_88, i32 2
	%_90 = load i8*, i8** %_89
	%_91 = load i8*, i8** %el01
	%_92 = bitcast i8* %_90 to i8* (i8* , i8*)*
	%_93 = call i8* %_92(i8* %_86 , i8* %_91)
	store i8* %_93, i8** %head
	%_94 = load i8*, i8** %head
	%_95 = bitcast i8* %_94 to i8***
	%_96 = load i8**, i8*** %_95
	%_97 = getelementptr i8*, i8** %_96, i32 9
	%_98 = load i8*, i8** %_97
	%_99 = bitcast i8* %_98 to i1 (i8* )*
	%_100 = call i1 %_99(i8* %_94 )
	store i1 %_100, i1* %aux01
	%_101 = call i8* @calloc(i32 1, i32 17)
	%_102 = bitcast i8* %_101 to i8***
	%_103 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_103, i8*** %_102
	store i8* %_101, i8** %el03
	%_104 = load i8*, i8** %el03
	%_105 = bitcast i8* %_104 to i8***
	%_106 = load i8**, i8*** %_105
	%_107 = getelementptr i8*, i8** %_106, i32 0
	%_108 = load i8*, i8** %_107
	%_109 = bitcast i8* %_108 to i1 (i8* , i32, i32, i1)*
	%_110 = call i1 %_109(i8* %_104 , i32 27, i32 34000, i1 0)
	store i1 %_110, i1* %aux01
	%_111 = load i8*, i8** %head
	%_112 = bitcast i8* %_111 to i8***
	%_113 = load i8**, i8*** %_112
	%_114 = getelementptr i8*, i8** %_113, i32 5
	%_115 = load i8*, i8** %_114
	%_116 = load i8*, i8** %el02
	%_117 = bitcast i8* %_115 to i32 (i8* , i8*)*
	%_118 = call i32 %_117(i8* %_111 , i8* %_116)
	call void (i32) @print_int(i32 %_118)
	%_119 = load i8*, i8** %head
	%_120 = bitcast i8* %_119 to i8***
	%_121 = load i8**, i8*** %_120
	%_122 = getelementptr i8*, i8** %_121, i32 5
	%_123 = load i8*, i8** %_122
	%_124 = load i8*, i8** %el03
	%_125 = bitcast i8* %_123 to i32 (i8* , i8*)*
	%_126 = call i32 %_125(i8* %_119 , i8* %_124)
	call void (i32) @print_int(i32 %_126)
	call void (i32) @print_int(i32 10000000)
	%_128 = call i8* @calloc(i32 1, i32 17)
	%_129 = bitcast i8* %_128 to i8***
	%_130 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_130, i8*** %_129
	store i8* %_128, i8** %el01
	%_131 = load i8*, i8** %el01
	%_132 = bitcast i8* %_131 to i8***
	%_133 = load i8**, i8*** %_132
	%_134 = getelementptr i8*, i8** %_133, i32 0
	%_135 = load i8*, i8** %_134
	%_136 = bitcast i8* %_135 to i1 (i8* , i32, i32, i1)*
	%_137 = call i1 %_136(i8* %_131 , i32 28, i32 35000, i1 0)
	store i1 %_137, i1* %aux01
	%_138 = load i8*, i8** %head
	%_139 = bitcast i8* %_138 to i8***
	%_140 = load i8**, i8*** %_139
	%_141 = getelementptr i8*, i8** %_140, i32 2
	%_142 = load i8*, i8** %_141
	%_143 = load i8*, i8** %el01
	%_144 = bitcast i8* %_142 to i8* (i8* , i8*)*
	%_145 = call i8* %_144(i8* %_138 , i8* %_143)
	store i8* %_145, i8** %head
	%_146 = load i8*, i8** %head
	%_147 = bitcast i8* %_146 to i8***
	%_148 = load i8**, i8*** %_147
	%_149 = getelementptr i8*, i8** %_148, i32 9
	%_150 = load i8*, i8** %_149
	%_151 = bitcast i8* %_150 to i1 (i8* )*
	%_152 = call i1 %_151(i8* %_146 )
	store i1 %_152, i1* %aux01
	call void (i32) @print_int(i32 2220000)
	%_153 = load i8*, i8** %head
	%_154 = bitcast i8* %_153 to i8***
	%_155 = load i8**, i8*** %_154
	%_156 = getelementptr i8*, i8** %_155, i32 4
	%_157 = load i8*, i8** %_156
	%_158 = load i8*, i8** %el02
	%_159 = bitcast i8* %_157 to i8* (i8* , i8*)*
	%_160 = call i8* %_159(i8* %_153 , i8* %_158)
	store i8* %_160, i8** %head
	%_161 = load i8*, i8** %head
	%_162 = bitcast i8* %_161 to i8***
	%_163 = load i8**, i8*** %_162
	%_164 = getelementptr i8*, i8** %_163, i32 9
	%_165 = load i8*, i8** %_164
	%_166 = bitcast i8* %_165 to i1 (i8* )*
	%_167 = call i1 %_166(i8* %_161 )
	store i1 %_167, i1* %aux01
	call void (i32) @print_int(i32 33300000)
	%_168 = load i8*, i8** %head
	%_169 = bitcast i8* %_168 to i8***
	%_170 = load i8**, i8*** %_169
	%_171 = getelementptr i8*, i8** %_170, i32 4
	%_172 = load i8*, i8** %_171
	%_173 = load i8*, i8** %el01
	%_174 = bitcast i8* %_172 to i8* (i8* , i8*)*
	%_175 = call i8* %_174(i8* %_168 , i8* %_173)
	store i8* %_175, i8** %head
	%_176 = load i8*, i8** %head
	%_177 = bitcast i8* %_176 to i8***
	%_178 = load i8**, i8*** %_177
	%_179 = getelementptr i8*, i8** %_178, i32 9
	%_180 = load i8*, i8** %_179
	%_181 = bitcast i8* %_180 to i1 (i8* )*
	%_182 = call i1 %_181(i8* %_176 )
	store i1 %_182, i1* %aux01
	call void (i32) @print_int(i32 44440000)


	ret i32 0

}

