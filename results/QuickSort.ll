@.QuickSort_vtable = global [0 x i8*] []

@.QS_vtable = global [4 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @QS.Start to i8*),
	i8* bitcast (i32 (i8*, i32, i32)* @QS.Sort to i8*),
	i8* bitcast (i32 (i8*)* @QS.Print to i8*),
	i8* bitcast (i32 (i8*, i32)* @QS.Init to i8*)
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
	%_2 = getelementptr [4 x i8*], [4 x i8*]* @.QS_vtable, i32 0, i32 0
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

define i32 @QS.Start(i8* %this, i32 %.sz) {
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
	call void (i32) @print_int(i32 9999)

	%_13 = getelementptr i8, i8* %this, i32 16
	%_14 = bitcast i8* %_13 to i32*
	%_15 = load i32, i32* %_14
	%_16 = sub i32 %_15, 1
	store i32 %_16, i32* %aux01
	%_17 = bitcast i8* %this to i8***
	%_18 = load i8**, i8*** %_17
	%_19 = getelementptr i8*, i8** %_18, i32 1
	%_20 = load i8*, i8** %_19
	%_21 = load i32, i32* %aux01
	%_22 = bitcast i8* %_20 to i32 (i8* , i32, i32)*
	%_23 = call i32 %_22(i8* %this , i32 0, i32 %_21)
	store i32 %_23, i32* %aux01
	%_24 = bitcast i8* %this to i8***
	%_25 = load i8**, i8*** %_24
	%_26 = getelementptr i8*, i8** %_25, i32 2
	%_27 = load i8*, i8** %_26
	%_28 = bitcast i8* %_27 to i32 (i8* )*
	%_29 = call i32 %_28(i8* %this )
	store i32 %_29, i32* %aux01


	ret i32 0

}

define i32 @QS.Sort(i8* %this, i32 %.left,i32 %.right) {
	%left = alloca i32
	store i32 %.left, i32* %left
	%right = alloca i32
	store i32 %.right, i32* %right
	%v = alloca i32
	%i = alloca i32
	%j = alloca i32
	%nt = alloca i32
	%t = alloca i32
	%cont01 = alloca i1
	%cont02 = alloca i1
	%aux03 = alloca i32
	store i32 0, i32* %t
	%_0 = load i32, i32* %left
	%_1 = load i32, i32* %right
	%_2 = icmp slt i32 %_0, %_1
	br i1 %_2, label %if_then_0, label %if_else_0


	if_then_0:

	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i32**
	%_5 = load i32*, i32** %_4
	%_6 = load i32, i32* %right
	%_7 = load i32, i32* %_5
	%_8 = icmp sge i32 %_6, 0
	%_9 = icmp slt i32 %_6, %_7
	%_10 = and i1 %_8, %_9
	br i1 %_10, label %oob_ok_1, label %oob_err_1

	oob_err_1:
	call void @throw_oob()
	br label %oob_ok_1

	oob_ok_1:
	%_11 = add i32 1, %_6
	%_12 = getelementptr i32, i32* %_5, i32 %_11
	%_13 = load i32, i32* %_12
	store i32 %_13, i32* %v
	%_14 = load i32, i32* %left
	%_15 = sub i32 %_14, 1
	store i32 %_15, i32* %i
	%_16 = load i32, i32* %right
	store i32 %_16, i32* %j
	store i1 1, i1* %cont01
	br label %loopstart2

	loopstart2:
	%_17 = load i1, i1* %cont01
	br i1 %_17, label %next2, label %end2

	next2:
	store i1 1, i1* %cont02
	br label %loopstart3

	loopstart3:
	%_18 = load i1, i1* %cont02
	br i1 %_18, label %next3, label %end3

	next3:
	%_19 = load i32, i32* %i
	%_20 = add i32 %_19, 1
	store i32 %_20, i32* %i

	%_21 = getelementptr i8, i8* %this, i32 8
	%_22 = bitcast i8* %_21 to i32**
	%_23 = load i32*, i32** %_22
	%_24 = load i32, i32* %i
	%_25 = load i32, i32* %_23
	%_26 = icmp sge i32 %_24, 0
	%_27 = icmp slt i32 %_24, %_25
	%_28 = and i1 %_26, %_27
	br i1 %_28, label %oob_ok_4, label %oob_err_4

	oob_err_4:
	call void @throw_oob()
	br label %oob_ok_4

	oob_ok_4:
	%_29 = add i32 1, %_24
	%_30 = getelementptr i32, i32* %_23, i32 %_29
	%_31 = load i32, i32* %_30
	store i32 %_31, i32* %aux03
	%_32 = load i32, i32* %aux03
	%_33 = load i32, i32* %v
	%_34 = icmp slt i32 %_32, %_33
	%_35 = xor i1 1, %_34
	br i1 %_35, label %if_then_5, label %if_else_5


	if_then_5:
	store i1 0, i1* %cont02
	br label %if_end_5

	if_else_5:
	store i1 1, i1* %cont02
	br label %if_end_5

	if_end_5:
	br label %loopstart3


	end3:
	store i1 1, i1* %cont02
	br label %loopstart8

	loopstart8:
	%_36 = load i1, i1* %cont02
	br i1 %_36, label %next8, label %end8

	next8:
	%_37 = load i32, i32* %j
	%_38 = sub i32 %_37, 1
	store i32 %_38, i32* %j

	%_39 = getelementptr i8, i8* %this, i32 8
	%_40 = bitcast i8* %_39 to i32**
	%_41 = load i32*, i32** %_40
	%_42 = load i32, i32* %j
	%_43 = load i32, i32* %_41
	%_44 = icmp sge i32 %_42, 0
	%_45 = icmp slt i32 %_42, %_43
	%_46 = and i1 %_44, %_45
	br i1 %_46, label %oob_ok_9, label %oob_err_9

	oob_err_9:
	call void @throw_oob()
	br label %oob_ok_9

	oob_ok_9:
	%_47 = add i32 1, %_42
	%_48 = getelementptr i32, i32* %_41, i32 %_47
	%_49 = load i32, i32* %_48
	store i32 %_49, i32* %aux03
	%_50 = load i32, i32* %v
	%_51 = load i32, i32* %aux03
	%_52 = icmp slt i32 %_50, %_51
	%_53 = xor i1 1, %_52
	br i1 %_53, label %if_then_10, label %if_else_10


	if_then_10:
	store i1 0, i1* %cont02
	br label %if_end_10

	if_else_10:
	store i1 1, i1* %cont02
	br label %if_end_10

	if_end_10:
	br label %loopstart8


	end8:

	%_54 = getelementptr i8, i8* %this, i32 8
	%_55 = bitcast i8* %_54 to i32**
	%_56 = load i32*, i32** %_55
	%_57 = load i32, i32* %i
	%_58 = load i32, i32* %_56
	%_59 = icmp sge i32 %_57, 0
	%_60 = icmp slt i32 %_57, %_58
	%_61 = and i1 %_59, %_60
	br i1 %_61, label %oob_ok_13, label %oob_err_13

	oob_err_13:
	call void @throw_oob()
	br label %oob_ok_13

	oob_ok_13:
	%_62 = add i32 1, %_57
	%_63 = getelementptr i32, i32* %_56, i32 %_62
	%_64 = load i32, i32* %_63
	store i32 %_64, i32* %t

	%_65 = getelementptr i8, i8* %this, i32 8
	%_66 = bitcast i8* %_65 to i32**
	%_67 = load i32*, i32** %_66
	%_68 = load i32, i32* %j
	%_69 = load i32, i32* %_67
	%_70 = icmp sge i32 %_68, 0
	%_71 = icmp slt i32 %_68, %_69
	%_72 = and i1 %_70, %_71
	br i1 %_72, label %oob_ok_14, label %oob_err_14

	oob_err_14:
	call void @throw_oob()
	br label %oob_ok_14

	oob_ok_14:
	%_73 = add i32 1, %_68
	%_74 = getelementptr i32, i32* %_67, i32 %_73
	%_75 = load i32, i32* %_74

	%_76 = getelementptr i8, i8* %this, i32 8
	%_77 = bitcast i8* %_76 to i32**
	%_78 = load i32*, i32** %_77
	%_79 = load i32, i32* %i
	%_80 = load i32, i32* %_78
	%_81 = icmp sge i32 %_79, 0
	%_82 = icmp slt i32 %_79, %_80
	%_83 = and i1 %_81, %_82
	br i1 %_83, label %oob_ok_15, label %oob_err_15

	oob_err_15:
	call void @throw_oob()
	br label %oob_ok_15

	oob_ok_15:
	%_84 = add i32 1, %_79
	%_85 = getelementptr i32, i32* %_78, i32 %_84
	store i32 %_75, i32* %_85

	%_86 = getelementptr i8, i8* %this, i32 8
	%_87 = bitcast i8* %_86 to i32**
	%_88 = load i32*, i32** %_87
	%_89 = load i32, i32* %j
	%_90 = load i32, i32* %t
	%_91 = load i32, i32* %_88
	%_92 = icmp sge i32 %_89, 0
	%_93 = icmp slt i32 %_89, %_91
	%_94 = and i1 %_92, %_93
	br i1 %_94, label %oob_ok_16, label %oob_err_16

	oob_err_16:
	call void @throw_oob()
	br label %oob_ok_16

	oob_ok_16:
	%_95 = add i32 1, %_89
	%_96 = getelementptr i32, i32* %_88, i32 %_95
	store i32 %_90, i32* %_96
	%_97 = load i32, i32* %i
	%_98 = add i32 %_97, 1
	%_99 = load i32, i32* %j
	%_100 = icmp slt i32 %_99, %_98
	br i1 %_100, label %if_then_17, label %if_else_17


	if_then_17:
	store i1 0, i1* %cont01
	br label %if_end_17

	if_else_17:
	store i1 1, i1* %cont01
	br label %if_end_17

	if_end_17:
	br label %loopstart2


	end2:

	%_101 = getelementptr i8, i8* %this, i32 8
	%_102 = bitcast i8* %_101 to i32**
	%_103 = load i32*, i32** %_102
	%_104 = load i32, i32* %i
	%_105 = load i32, i32* %_103
	%_106 = icmp sge i32 %_104, 0
	%_107 = icmp slt i32 %_104, %_105
	%_108 = and i1 %_106, %_107
	br i1 %_108, label %oob_ok_20, label %oob_err_20

	oob_err_20:
	call void @throw_oob()
	br label %oob_ok_20

	oob_ok_20:
	%_109 = add i32 1, %_104
	%_110 = getelementptr i32, i32* %_103, i32 %_109
	%_111 = load i32, i32* %_110

	%_112 = getelementptr i8, i8* %this, i32 8
	%_113 = bitcast i8* %_112 to i32**
	%_114 = load i32*, i32** %_113
	%_115 = load i32, i32* %j
	%_116 = load i32, i32* %_114
	%_117 = icmp sge i32 %_115, 0
	%_118 = icmp slt i32 %_115, %_116
	%_119 = and i1 %_117, %_118
	br i1 %_119, label %oob_ok_21, label %oob_err_21

	oob_err_21:
	call void @throw_oob()
	br label %oob_ok_21

	oob_ok_21:
	%_120 = add i32 1, %_115
	%_121 = getelementptr i32, i32* %_114, i32 %_120
	store i32 %_111, i32* %_121

	%_122 = getelementptr i8, i8* %this, i32 8
	%_123 = bitcast i8* %_122 to i32**
	%_124 = load i32*, i32** %_123
	%_125 = load i32, i32* %right
	%_126 = load i32, i32* %_124
	%_127 = icmp sge i32 %_125, 0
	%_128 = icmp slt i32 %_125, %_126
	%_129 = and i1 %_127, %_128
	br i1 %_129, label %oob_ok_22, label %oob_err_22

	oob_err_22:
	call void @throw_oob()
	br label %oob_ok_22

	oob_ok_22:
	%_130 = add i32 1, %_125
	%_131 = getelementptr i32, i32* %_124, i32 %_130
	%_132 = load i32, i32* %_131

	%_133 = getelementptr i8, i8* %this, i32 8
	%_134 = bitcast i8* %_133 to i32**
	%_135 = load i32*, i32** %_134
	%_136 = load i32, i32* %i
	%_137 = load i32, i32* %_135
	%_138 = icmp sge i32 %_136, 0
	%_139 = icmp slt i32 %_136, %_137
	%_140 = and i1 %_138, %_139
	br i1 %_140, label %oob_ok_23, label %oob_err_23

	oob_err_23:
	call void @throw_oob()
	br label %oob_ok_23

	oob_ok_23:
	%_141 = add i32 1, %_136
	%_142 = getelementptr i32, i32* %_135, i32 %_141
	store i32 %_132, i32* %_142

	%_143 = getelementptr i8, i8* %this, i32 8
	%_144 = bitcast i8* %_143 to i32**
	%_145 = load i32*, i32** %_144
	%_146 = load i32, i32* %right
	%_147 = load i32, i32* %t
	%_148 = load i32, i32* %_145
	%_149 = icmp sge i32 %_146, 0
	%_150 = icmp slt i32 %_146, %_148
	%_151 = and i1 %_149, %_150
	br i1 %_151, label %oob_ok_24, label %oob_err_24

	oob_err_24:
	call void @throw_oob()
	br label %oob_ok_24

	oob_ok_24:
	%_152 = add i32 1, %_146
	%_153 = getelementptr i32, i32* %_145, i32 %_152
	store i32 %_147, i32* %_153
	%_154 = load i32, i32* %i
	%_155 = sub i32 %_154, 1
	%_156 = bitcast i8* %this to i8***
	%_157 = load i8**, i8*** %_156
	%_158 = getelementptr i8*, i8** %_157, i32 1
	%_159 = load i8*, i8** %_158
	%_160 = load i32, i32* %left
	%_161 = bitcast i8* %_159 to i32 (i8* , i32, i32)*
	%_162 = call i32 %_161(i8* %this , i32 %_160, i32 %_155)
	store i32 %_162, i32* %nt
	%_163 = load i32, i32* %i
	%_164 = add i32 %_163, 1
	%_165 = bitcast i8* %this to i8***
	%_166 = load i8**, i8*** %_165
	%_167 = getelementptr i8*, i8** %_166, i32 1
	%_168 = load i8*, i8** %_167
	%_169 = load i32, i32* %right
	%_170 = bitcast i8* %_168 to i32 (i8* , i32, i32)*
	%_171 = call i32 %_170(i8* %this , i32 %_164, i32 %_169)
	store i32 %_171, i32* %nt
	br label %if_end_0

	if_else_0:
	store i32 0, i32* %nt
	br label %if_end_0

	if_end_0:


	ret i32 0

}

define i32 @QS.Print(i8* %this) {
	%j = alloca i32
	store i32 0, i32* %j
	br label %loopstart26

	loopstart26:
	%_0 = load i32, i32* %j

	%_1 = getelementptr i8, i8* %this, i32 16
	%_2 = bitcast i8* %_1 to i32*
	%_3 = load i32, i32* %_2
	%_4 = icmp slt i32 %_0, %_3
	br i1 %_4, label %next26, label %end26

	next26:

	%_5 = getelementptr i8, i8* %this, i32 8
	%_6 = bitcast i8* %_5 to i32**
	%_7 = load i32*, i32** %_6
	%_8 = load i32, i32* %j
	%_9 = load i32, i32* %_7
	%_10 = icmp sge i32 %_8, 0
	%_11 = icmp slt i32 %_8, %_9
	%_12 = and i1 %_10, %_11
	br i1 %_12, label %oob_ok_27, label %oob_err_27

	oob_err_27:
	call void @throw_oob()
	br label %oob_ok_27

	oob_ok_27:
	%_13 = add i32 1, %_8
	%_14 = getelementptr i32, i32* %_7, i32 %_13
	%_15 = load i32, i32* %_14
	call void (i32) @print_int(i32 %_15)
	%_17 = load i32, i32* %j
	%_18 = add i32 %_17, 1
	store i32 %_18, i32* %j
	br label %loopstart26


	end26:


	ret i32 0

}

define i32 @QS.Init(i8* %this, i32 %.sz) {
	%sz = alloca i32
	store i32 %.sz, i32* %sz
	%_0 = load i32, i32* %sz

	%_1 = getelementptr i8, i8* %this, i32 16
	%_2 = bitcast i8* %_1 to i32*
	store i32 %_0, i32* %_2
	%_3 = load i32, i32* %sz
	%_4 = add i32 1, %_3
	%_5 = icmp sge i32 %_4, 1
	br i1 %_5, label %nsz_ok_29, label %nsz_err_29

	nsz_err_29:
	call void @throw_nsz()
	br label %nsz_ok_29

	nsz_ok_29:
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
	br i1 %_16, label %oob_ok_30, label %oob_err_30

	oob_err_30:
	call void @throw_oob()
	br label %oob_ok_30

	oob_ok_30:
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
	br i1 %_25, label %oob_ok_31, label %oob_err_31

	oob_err_31:
	call void @throw_oob()
	br label %oob_ok_31

	oob_ok_31:
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
	br i1 %_34, label %oob_ok_32, label %oob_err_32

	oob_err_32:
	call void @throw_oob()
	br label %oob_ok_32

	oob_ok_32:
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
	br i1 %_43, label %oob_ok_33, label %oob_err_33

	oob_err_33:
	call void @throw_oob()
	br label %oob_ok_33

	oob_ok_33:
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
	br i1 %_52, label %oob_ok_34, label %oob_err_34

	oob_err_34:
	call void @throw_oob()
	br label %oob_ok_34

	oob_ok_34:
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
	br i1 %_61, label %oob_ok_35, label %oob_err_35

	oob_err_35:
	call void @throw_oob()
	br label %oob_ok_35

	oob_ok_35:
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
	br i1 %_70, label %oob_ok_36, label %oob_err_36

	oob_err_36:
	call void @throw_oob()
	br label %oob_ok_36

	oob_ok_36:
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
	br i1 %_79, label %oob_ok_37, label %oob_err_37

	oob_err_37:
	call void @throw_oob()
	br label %oob_ok_37

	oob_ok_37:
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
	br i1 %_88, label %oob_ok_38, label %oob_err_38

	oob_err_38:
	call void @throw_oob()
	br label %oob_ok_38

	oob_ok_38:
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
	br i1 %_97, label %oob_ok_39, label %oob_err_39

	oob_err_39:
	call void @throw_oob()
	br label %oob_ok_39

	oob_ok_39:
	%_98 = add i32 1, 9
	%_99 = getelementptr i32, i32* %_93, i32 %_98
	store i32 5, i32* %_99


	ret i32 0

}

