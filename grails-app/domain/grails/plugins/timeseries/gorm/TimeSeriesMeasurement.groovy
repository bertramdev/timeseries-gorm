package grails.plugins.timeseries.gorm

class TimeSeriesMeasurement {
	String refId
	String metric
	String resolution
	Integer duration = 0
	Integer count = 0
	Double total = 0
	Date start
	Date end

	Double col0
	Double col1
	Double col2
	Double col3
	Double col4
	Double col5
	Double col6
	Double col7
	Double col8
	Double col9
	Double col10
	Double col11
	Double col12
	Double col13
	Double col14
	Double col15
	Double col16
	Double col17
	Double col18
	Double col19
	Double col20
	Double col21
	Double col22
	Double col23
	Double col24
	Double col25
	Double col26
	Double col27
	Double col28
	Double col29
	Double col30
	Double col31
	Double col32
	Double col33
	Double col34
	Double col35
	Double col36
	Double col37
	Double col38
	Double col39
	Double col40
	Double col41
	Double col42
	Double col43
	Double col44
	Double col45
	Double col46
	Double col47
	Double col48
	Double col49
	Double col50
	Double col51
	Double col52
	Double col53
	Double col54
	Double col55
	Double col56
	Double col57
	Double col58
	Double col59
	Double col60
	Double col61
	Double col62
	Double col63
	Double col64
	Double col65
	Double col66
	Double col67
	Double col68
	Double col69
	Double col70
	Double col71
	Double col72
	Double col73
	Double col74
	Double col75
	Double col76
	Double col77
	Double col78
	Double col79
	Double col80
	Double col81
	Double col82
	Double col83
	Double col84
	Double col85
	Double col86
	Double col87
	Double col88
	Double col89
	Double col90
	Double col91
	Double col92
	Double col93
	Double col94
	Double col95

	static mapping = {
		count column:'ct'
		refId index: 'idx_st_ref_met'
		start index: 'idx_st_ref_met'
		metric index: 'idx_st_ref_met,idx_ed_met'
		end index: 'idx_ed_met'
	}

    static constraints = {
		col0 nullable:true
		col1 nullable:true
		col2 nullable:true
		col3 nullable:true
		col4 nullable:true
		col5 nullable:true
		col6 nullable:true
		col7 nullable:true
		col8 nullable:true
		col9 nullable:true
		col10 nullable:true
		col11 nullable:true
		col12 nullable:true
		col13 nullable:true
		col14 nullable:true
		col15 nullable:true
		col16 nullable:true
		col17 nullable:true
		col18 nullable:true
		col19 nullable:true
		col20 nullable:true
		col21 nullable:true
		col22 nullable:true
		col23 nullable:true
		col24 nullable:true
		col25 nullable:true
		col26 nullable:true
		col27 nullable:true
		col28 nullable:true
		col29 nullable:true
		col30 nullable:true
		col31 nullable:true
		col32 nullable:true
		col33 nullable:true
		col34 nullable:true
		col35 nullable:true
		col36 nullable:true
		col37 nullable:true
		col38 nullable:true
		col39 nullable:true
		col40 nullable:true
		col41 nullable:true
		col42 nullable:true
		col43 nullable:true
		col44 nullable:true
		col45 nullable:true
		col46 nullable:true
		col47 nullable:true
		col48 nullable:true
		col49 nullable:true
		col50 nullable:true
		col51 nullable:true
		col52 nullable:true
		col53 nullable:true
		col54 nullable:true
		col55 nullable:true
		col56 nullable:true
		col57 nullable:true
		col58 nullable:true
		col59 nullable:true
		col60 nullable:true
		col61 nullable:true
		col62 nullable:true
		col63 nullable:true
		col64 nullable:true
		col65 nullable:true
		col66 nullable:true
		col67 nullable:true
		col68 nullable:true
		col69 nullable:true
		col70 nullable:true
		col71 nullable:true
		col72 nullable:true
		col73 nullable:true
		col74 nullable:true
		col75 nullable:true
		col76 nullable:true
		col77 nullable:true
		col78 nullable:true
		col79 nullable:true
		col80 nullable:true
		col81 nullable:true
		col82 nullable:true
		col83 nullable:true
		col84 nullable:true
		col85 nullable:true
		col86 nullable:true
		col87 nullable:true
		col88 nullable:true
		col89 nullable:true
		col90 nullable:true
		col91 nullable:true
		col92 nullable:true
		col93 nullable:true
		col94 nullable:true
		col95 nullable:true
    }
}
