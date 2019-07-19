// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.configuration.item;

import java.time.DayOfWeek;

import cat.mvmike.minimalcalendarwidget.R;

public enum Theme {

    BLACK(
        R.layout.widget_black,
        R.layout.black_cell_header,
        R.layout.black_cell_header_saturday,
        R.layout.black_cell_header_sunday,
        R.layout.black_cell_day,
        R.layout.black_cell_day_this_month,
        R.layout.black_cell_day_saturday,
        R.layout.black_cell_day_sunday,
        R.layout.black_cell_day_today,
        R.layout.black_cell_day_saturday_today,
        R.layout.black_cell_day_sunday_today
    ),

    GREY(
        R.layout.widget_grey,
        R.layout.grey_cell_header,
        R.layout.grey_cell_header_saturday,
        R.layout.grey_cell_header_sunday,
        R.layout.grey_cell_day,
        R.layout.grey_cell_day_this_month,
        R.layout.grey_cell_day_saturday,
        R.layout.grey_cell_day_sunday,
        R.layout.grey_cell_day_today,
        R.layout.grey_cell_day_saturday_today,
        R.layout.grey_cell_day_sunday_today
    ),

    WHITE(
        R.layout.widget_white,
        R.layout.white_cell_header,
        R.layout.white_cell_header_saturday,
        R.layout.white_cell_header_sunday,
        R.layout.white_cell_day,
        R.layout.white_cell_day_this_month,
        R.layout.white_cell_day_saturday,
        R.layout.white_cell_day_sunday,
        R.layout.white_cell_day_today,
        R.layout.white_cell_day_saturday_today,
        R.layout.white_cell_day_sunday_today
    );

    private final int mainLayout;

    private final int cellHeader;

    private final int cellHeaderSaturday;

    private final int cellHeaderSunday;

    private final int cellDay;

    private final int cellDayThisMonth;

    private final int cellDaySaturday;

    private final int cellDaySunday;

    private final int cellDayToday;

    private final int cellDaySaturdayToday;

    private final int cellDaySundayToday;

    Theme(final int mainLayout, final int cellHeader, final int cellHeaderSaturday, final int cellHeaderSunday, final int cellDay,
          final int cellDayThisMonth, final int cellDaySaturday, final int cellDaySunday, final int cellDayToday,
          final int cellDaySaturdayToday, final int cellDaySundayToday) {

        this.mainLayout = mainLayout;
        this.cellHeader = cellHeader;
        this.cellHeaderSaturday = cellHeaderSaturday;
        this.cellHeaderSunday = cellHeaderSunday;
        this.cellDay = cellDay;
        this.cellDayThisMonth = cellDayThisMonth;
        this.cellDaySaturday = cellDaySaturday;
        this.cellDaySunday = cellDaySunday;
        this.cellDayToday = cellDayToday;
        this.cellDaySaturdayToday = cellDaySaturdayToday;
        this.cellDaySundayToday = cellDaySundayToday;
    }

    public int getMainLayout() {
        return mainLayout;
    }

    public int getCellHeader() {
        return cellHeader;
    }

    public int getCellHeaderSaturday() {
        return cellHeaderSaturday;
    }

    public int getCellHeaderSunday() {
        return cellHeaderSunday;
    }

    public int getCellToday(final DayOfWeek dayOfWeek) {

        switch (dayOfWeek) {
            case SATURDAY:
                return cellDaySaturdayToday;
            case SUNDAY:
                return cellDaySundayToday;
            default:
                return cellDayToday;
        }
    }

    public int getCellThisMonth(final DayOfWeek dayOfWeek) {

        switch (dayOfWeek) {
            case SATURDAY:
                return cellDaySaturday;
            case SUNDAY:
                return cellDaySunday;
            default:
                return cellDayThisMonth;
        }
    }

    public int getCellNotThisMonth() {
        return cellDay;
    }
}
