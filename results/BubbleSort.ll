@.BubbleSort_vtable = global [0 x i8*] []

@.BBS_vtable = global [4 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @BBS.Start to i8*),
	i8* bitcast (i32 (i8*)* @BBS.Sort to i8*),
	i8* bitcast (i32 (i8*)* @BBS.Print to i8*),
	i8* bitcast (i32 (i8*, i32)* @BBS.Init to i8*)
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
	%_0 = call i8* @calloc(i32 1, i32 20)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [4 x i8*], [4 x i8*]* @.BBS_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32 (i8* , i32)*
	%_8 = call i32 %_7(i8* %_0 , i32 10)
	call void (i32) @print_int(i32 %_8)
	ret i32 0

}

define i32 @BBS.Start(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32 %.sz, i32* %sz
	%aux01 = alloca i32
	%_0 = bitcast i8* %this to i8***
	%_1 = load i8**, i8*** %_0
	%_2 = getelementptr i8*, i8** %_1, i32 3
	%_3 = load i8*, i8** %_2
	%_4 = load i32, i32* %sz
	%_5 = bitcast i8* %_3 to i32 (i8* , i32)*
	%_6 = call i32 %_5(i8* %this , i32 %_4)
	store i32 %_6, i32* %aux01
	%_7 = bitcast i8* %this to i8***
	%_8 = load i8**, i8*** %_7
	%_9 = getelementptr i8*, i8** %_8, i32 2
	%_10 = load i8*, i8** %_9
	%_11 = bitcast i8* %_10 to i32 (i8* )*
	%_12 = call i32 %_11(i8* %this )
	store i32 %_12, i32* %aux01
	call void (i32) @print_int(i32 99999)
	%_13 = bitcast i8* %this to i8***
	%_14 = load i8**, i8*** %_13
	%_15 = getelementptr i8*, i8** %_14, i32 1
	%_16 = load i8*, i8** %_15
	%_17 = bitcast i8* %_16 to i32 (i8* )*
	%_18 = call i32 %_17(i8* %this )
	store i32 %_18, i32* %aux01
	%_19 = bitcast i8* %this to i8***
	%_20 = load i8**, i8*** %_19
	%_21 = getelementptr i8*, i8** %_20, i32 2
	%_22 = load i8*, i8** %_21
	%_23 = bitcast i8* %_22 to i32 (i8* )*
	%_24 = call i32 %_23(i8* %this )
	store i32 %_24, i32* %aux01


	ret i32 0

}

define i32 @BBS.Sort(i8* %this) {
	%nt = alloca i32
	%i = alloca i32
	%aux02 = alloca i32
	%aux04 = alloca i32
	%aux05 = alloca i32
	%aux06 = alloca i32
	%aux07 = alloca i32
	%j = alloca i32
	%t = alloca i32

	%_0 = getelementptr i8, i8* %this, i32 16
	%_1 = bitcast i8* %_0 to i32*
	%_2 = load i32, i32* %_1
	%_3 = sub i32 %_2, 1
	store i32 %_3, i32* %i
	%_4 = sub i32 0, 1
	store i32 %_4, i32* %aux02
	br label %loopstart0

	loopstart0:
	%_5 = load i32, i32* %aux02
	%_6 = load i32, i32* %i
	%_7 = icmp slt i32 %_5, %_6
	br i1 %_7, label %next0, label %end0

	next0:
	store i32 1, i32* %j
	br label %loopstart1

	loopstart1:
	%_8 = load i32, i32* %i
	%_9 = add i32 %_8, 1
	%_10 = load i32, i32* %j
	%_11 = icmp slt i32 %_10, %_9
	br i1 %_11, label %next1, label %end1

	next1:
	%_12 = load i32, i32* %j
	%_13 = sub i32 %_12, 1
	store i32 %_13, i32* %aux07

	%_14 = getelementptr i8, i8* %this, i32 8
	%_15 = bitcast i8* %_14 to i32**
	%_16 = load i32*, i32** %_15
	%_17 = load i32, i32* %aux07
	%_18 = load i32, i32* %_16
	%_19 = icmp sge i32 %_17, 0
	%_20 = icmp slt i32 %_17, %_18
	%_21 = and i1 %_19, %_20
	br i1 %_21, label %oob_ok_2, label %oob_err_2

	oob_err_2:
	call void @throw_oob()
	br label %oob_ok_2

	oob_ok_2:
	%_22 = add i32 1, %_17
	%_23 = getelementptr i32, i32* %_16, i32 %_22
	%_24 = load i32, i32* %_23
	store i32 %_24, i32* %aux04

	%_25 = getelementptr i8, i8* %this, i32 8
	%_26 = bitcast i8* %_25 to i32**
	%_27 = load i32*, i32** %_26
	%_28 = load i32, i32* %j
	%_29 = load i32, i32* %_27
	%_30 = icmp sge i32 %_28, 0
	%_31 = icmp slt i32 %_28, %_29
	%_32 = and i1 %_30, %_31
	br i1 %_32, label %oob_ok_3, label %oob_err_3

	oob_err_3:
	call void @throw_oob()
	br label %oob_ok_3

	oob_ok_3:
	%_33 = add i32 1, %_28
	%_34 = getelementptr i32, i32* %_27, i32 %_33
	%_35 = load i32, i32* %_34
	store i32 %_35, i32* %aux05
	%_36 = load i32, i32* %aux05
	%_37 = load i32, i32* %aux04
	%_38 = icmp slt i32 %_36, %_37
	br i1 %_38, label %if_then_4, label %if_else_4


	if_then_4:
	%_39 = load i32, i32* %j
	%_40 = sub i32 %_39, 1
	store i32 %_40, i32* %aux06

	%_41 = getelementptr i8, i8* %this, i32 8
	%_42 = bitcast i8* %_41 to i32**
	%_43 = load i32*, i32** %_42
	%_44 = load i32, i32* %aux06
	%_45 = load i32, i32* %_43
	%_46 = icmp sge i32 %_44, 0
	%_47 = icmp slt i32 %_44, %_45
	%_48 = and i1 %_46, %_47
	br i1 %_48, label %oob_ok_5, label %oob_err_5

	oob_err_5:
	call void @throw_oob()
	br label %oob_ok_5

	oob_ok_5:
	%_49 = add i32 1, %_44
	%_50 = getelementptr i32, i32* %_43, i32 %_49
	%_51 = load i32, i32* %_50
	store i32 %_51, i32* %t

	%_52 = getelementptr i8, i8* %this, i32 8
	%_53 = bitcast i8* %_52 to i32**
	%_54 = load i32*, i32** %_53
	%_55 = load i32, i32* %j
	%_56 = load i32, i32* %_54
	%_57 = icmp sge i32 %_55, 0
	%_58 = icmp slt i32 %_55, %_56
	%_59 = and i1 %_57, %_58
	br i1 %_59, label %oob_ok_6, label %oob_err_6

	oob_err_6:
	call void @throw_oob()
	br label %oob_ok_6

	oob_ok_6:
	%_60 = add i32 1, %_55
	%_61 = getelementptr i32, i32* %_54, i32 %_60
	%_62 = load i32, i32* %_61

	%_63 = getelementptr i8, i8* %this, i32 8
	%_64 = bitcast i8* %_63 to i32**
	%_65 = load i32*, i32** %_64
	%_66 = load i32, i32* %aux06
	%_67 = load i32, i32* %_65
	%_68 = icmp sge i32 %_66, 0
	%_69 = icmp slt i32 %_66, %_67
	%_70 = and i1 %_68, %_69
	br i1 %_70, label %oob_ok_7, label %oob_err_7

	oob_err_7:
	call void @throw_oob()
	br label %oob_ok_7

	oob_ok_7:
	%_71 = add i32 1, %_66
	%_72 = getelementptr i32, i32* %_65, i32 %_71
	store i32 %_62, i32* %_72

	%_73 = getelementptr i8, i8* %this, i32 8
	%_74 = bitcast i8* %_73 to i32**
	%_75 = load i32*, i32** %_74
	%_76 = load i32, i32* %j
	%_77 = load i32, i32* %t
	%_78 = load i32, i32* %_75
	%_79 = icmp sge i32 %_76, 0
	%_80 = icmp slt i32 %_76, %_78
	%_81 = and i1 %_79, %_80
	br i1 %_81, label %oob_ok_8, label %oob_err_8

	oob_err_8:
	call void @throw_oob()
	br label %oob_ok_8

	oob_ok_8:
	%_82 = add i32 1, %_76
	%_83 = getelementptr i32, i32* %_75, i32 %_82
	store i32 %_77, i32* %_83
	br label %if_end_4

	if_else_4:
	store i32 0, i32* %nt
	br label %if_end_4

	if_end_4:
	%_84 = load i32, i32* %j
	%_85 = add i32 %_84, 1
	store i32 %_85, i32* %j
	br label %loopstart1


	end1:
	%_86 = load i32, i32* %i
	%_87 = sub i32 %_86, 1
	store i32 %_87, i32* %i
	br label %loopstart0


	end0:


	ret i32 0

}

define i32 @BBS.Print(i8* %this) {
	%j = alloca i32
	store i32 0, i32* %j
	br label %loopstart12

	loopstart12:
	%_0 = load i32, i32* %j

	%_1 = getelementptr i8, i8* %this, i32 16
	%_2 = bitcast i8* %_1 to i32*
	%_3 = load i32, i32* %_2
	%_4 = icmp slt i32 %_0, %_3
	br i1 %_4, label %next12, label %end12

	next12:

	%_5 = getelementptr i8, i8* %this, i32 8
	%_6 = bitcast i8* %_5 to i32**
	%_7 = load i32*, i32** %_6
	%_8 = load i32, i32* %j
	%_9 = load i32, i32* %_7
	%_10 = icmp sge i32 %_8, 0
	%_11 = icmp slt i32 %_8, %_9
	%_12 = and i1 %_10, %_11
	br i1 %_12, label %oob_ok_13, label %oob_err_13

	oob_err_13:
	call void @throw_oob()
	br label %oob_ok_13

	oob_ok_13:
	%_13 = add i32 1, %_8
	%_14 = getelementptr i32, i32* %_7, i32 %_13
	%_15 = load i32, i32* %_14
	call void (i32) @print_int(i32 %_15)
	%_17 = load i32, i32* %j
	%_18 = add i32 %_17, 1
	store i32 %_18, i32* %j
	br label %loopstart12


	end12:


	ret i32 0

}

define i32 @BBS.Init(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32 %.sz, i32* %sz
	%_0 = load i32, i32* %sz

	%_1 = getelementptr i8, i8* %this, i32 16
	%_2 = bitcast i8* %_1 to i32*
	store i32 %_0, i32* %_2
	%_3 = load i32, i32* %sz
	%_4 = add i32 1, %_3
	%_5 = icmp sge i32 %_4, 1
	br i1 %_5, label %nsz_ok_15, label %nsz_err_15

	nsz_err_15:
	call void @throw_nsz()
	br label %nsz_ok_15

	nsz_ok_15:
	%_6 = call i8* @calloc(i32 %_4, i32 4)
	%_7 = bitcast i8* %_6 to i32*
	store i32 %_3, i32* %_7

	%_8 = getelementptr i8, i8* %this, i32 8
	%_9 = bitcast i8* %_8 to i32**
	store i32* %_7, i32** %_9

	%_10 = getelementptr i8, i8* %this, i32 8
	%_11 = bitcast i8* %_10 to i32**
	%_12 = load i32*, i32** %_11
	%_13 = load i32, i32* %_12
	%_14 = icmp sge i32 0, 0
	%_15 = icmp slt i32 0, %_13
	%_16 = and i1 %_14, %_15
	br i1 %_16, label %oob_ok_16, label %oob_err_16

	oob_err_16:
	call void @throw_oob()
	br label %oob_ok_16

	oob_ok_16:
	%_17 = add i32 1, 0
	%_18 = getelementptr i32, i32* %_12, i32 %_17
	store i32 20, i32* %_18

	%_19 = getelementptr i8, i8* %this, i32 8
	%_20 = bitcast i8* %_19 to i32**
	%_21 = load i32*, i32** %_20
	%_22 = load i32, i32* %_21
	%_23 = icmp sge i32 1, 0
	%_24 = icmp slt i32 1, %_22
	%_25 = and i1 %_23, %_24
	br i1 %_25, label %oob_ok_17, label %oob_err_17

	oob_err_17:
	call void @throw_oob()
	br label %oob_ok_17

	oob_ok_17:
	%_26 = add i32 1, 1
	%_27 = getelementptr i32, i32* %_21, i32 %_26
	store i32 7, i32* %_27

	%_28 = getelementptr i8, i8* %this, i32 8
	%_29 = bitcast i8* %_28 to i32**
	%_30 = load i32*, i32** %_29
	%_31 = load i32, i32* %_30
	%_32 = icmp sge i32 2, 0
	%_33 = icmp slt i32 2, %_31
	%_34 = and i1 %_32, %_33
	br i1 %_34, label %oob_ok_18, label %oob_err_18

	oob_err_18:
	call void @throw_oob()
	br label %oob_ok_18

	oob_ok_18:
	%_35 = add i32 1, 2
	%_36 = getelementptr i32, i32* %_30, i32 %_35
	store i32 12, i32* %_36

	%_37 = getelementptr i8, i8* %this, i32 8
	%_38 = bitcast i8* %_37 to i32**
	%_39 = load i32*, i32** %_38
	%_40 = load i32, i32* %_39
	%_41 = icmp sge i32 3, 0
	%_42 = icmp slt i32 3, %_40
	%_43 = and i1 %_41, %_42
	br i1 %_43, label %oob_ok_19, label %oob_err_19

	oob_err_19:
	call void @throw_oob()
	br label %oob_ok_19

	oob_ok_19:
	%_44 = add i32 1, 3
	%_45 = getelementptr i32, i32* %_39, i32 %_44
	store i32 18, i32* %_45

	%_46 = getelementptr i8, i8* %this, i32 8
	%_47 = bitcast i8* %_46 to i32**
	%_48 = load i32*, i32** %_47
	%_49 = load i32, i32* %_48
	%_50 = icmp sge i32 4, 0
	%_51 = icmp slt i32 4, %_49
	%_52 = and i1 %_50, %_51
	br i1 %_52, label %oob_ok_20, label %oob_err_20

	oob_err_20:
	call void @throw_oob()
	br label %oob_ok_20

	oob_ok_20:
	%_53 = add i32 1, 4
	%_54 = getelementptr i32, i32* %_48, i32 %_53
	store i32 2, i32* %_54

	%_55 = getelementptr i8, i8* %this, i32 8
	%_56 = bitcast i8* %_55 to i32**
	%_57 = load i32*, i32** %_56
	%_58 = load i32, i32* %_57
	%_59 = icmp sge i32 5, 0
	%_60 = icmp slt i32 5, %_58
	%_61 = and i1 %_59, %_60
	br i1 %_61, label %oob_ok_21, label %oob_err_21

	oob_err_21:
	call void @throw_oob()
	br label %oob_ok_21

	oob_ok_21:
	%_62 = add i32 1, 5
	%_63 = getelementptr i32, i32* %_57, i32 %_62
	store i32 11, i32* %_63

	%_64 = getelementptr i8, i8* %this, i32 8
	%_65 = bitcast i8* %_64 to i32**
	%_66 = load i32*, i32** %_65
	%_67 = load i32, i32* %_66
	%_68 = icmp sge i32 6, 0
	%_69 = icmp slt i32 6, %_67
	%_70 = and i1 %_68, %_69
	br i1 %_70, label %oob_ok_22, label %oob_err_22

	oob_err_22:
	call void @throw_oob()
	br label %oob_ok_22

	oob_ok_22:
	%_71 = add i32 1, 6
	%_72 = getelementptr i32, i32* %_66, i32 %_71
	store i32 6, i32* %_72

	%_73 = getelementptr i8, i8* %this, i32 8
	%_74 = bitcast i8* %_73 to i32**
	%_75 = load i32*, i32** %_74
	%_76 = load i32, i32* %_75
	%_77 = icmp sge i32 7, 0
	%_78 = icmp slt i32 7, %_76
	%_79 = and i1 %_77, %_78
	br i1 %_79, label %oob_ok_23, label %oob_err_23

	oob_err_23:
	call void @throw_oob()
	br label %oob_ok_23

	oob_ok_23:
	%_80 = add i32 1, 7
	%_81 = getelementptr i32, i32* %_75, i32 %_80
	store i32 9, i32* %_81

	%_82 = getelementptr i8, i8* %this, i32 8
	%_83 = bitcast i8* %_82 to i32**
	%_84 = load i32*, i32** %_83
	%_85 = load i32, i32* %_84
	%_86 = icmp sge i32 8, 0
	%_87 = icmp slt i32 8, %_85
	%_88 = and i1 %_86, %_87
	br i1 %_88, label %oob_ok_24, label %oob_err_24

	oob_err_24:
	call void @throw_oob()
	br label %oob_ok_24

	oob_ok_24:
	%_89 = add i32 1, 8
	%_90 = getelementptr i32, i32* %_84, i32 %_89
	store i32 19, i32* %_90

	%_91 = getelementptr i8, i8* %this, i32 8
	%_92 = bitcast i8* %_91 to i32**
	%_93 = load i32*, i32** %_92
	%_94 = load i32, i32* %_93
	%_95 = icmp sge i32 9, 0
	%_96 = icmp slt i32 9, %_94
	%_97 = and i1 %_95, %_96
	br i1 %_97, label %oob_ok_25, label %oob_err_25

	oob_err_25:
	call void @throw_oob()
	br label %oob_ok_25

	oob_ok_25:
	%_98 = add i32 1, 9
	%_99 = getelementptr i32, i32* %_93, i32 %_98
	store i32 5, i32* %_99


	ret i32 0

}

