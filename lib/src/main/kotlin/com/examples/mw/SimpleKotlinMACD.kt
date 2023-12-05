package com.examples.mw

import com.motivewave.platform.sdk.common.*
import com.motivewave.platform.sdk.common.desc.*
import com.motivewave.platform.sdk.study.Study
import com.motivewave.platform.sdk.study.StudyHeader

/** Simple MACD example. This example shows how to create a Study Graph that is based on the MACD study.
 * For simplicity, some code from the original MotiveWave MACD study has been removed or altered. */
@StudyHeader(
    namespace = "com.examples",
    id = "SimpleKotlinMACD",
    name = "Simple Kotlin MACD",
    desc = "This is a simple Kotlin version of the <b>MACD</b> for example purposes.",
    menu = "Examples",
    overlay = false
)
class SimpleKotlinMACD : Study() {
    // This enumeration defines the variables that we are going to store in the Data Series
    internal enum class Values {
        MACD,
        SIGNAL,
        HIST
    }

    /** This method initializes the settings and defines the runtime settings.  */
    override fun initialize(defaults: Defaults) {
        // Define the settings for this study
        // We are creating 2 tabs: 'General' and 'Display'
        val sd = createSD()
        var tab = sd.addTab("General")

        // Define the 'Inputs'
        var grp = tab.addGroup("Inputs")
        grp.addRow(InputDescriptor(Inputs.INPUT, "Input", Enums.BarInput.CLOSE))
        grp.addRow(IntegerDescriptor(Inputs.PERIOD, "Period 1", 12, 1, 9999, 1))
        grp.addRow(IntegerDescriptor(Inputs.PERIOD2, "Period 2", 26, 1, 9999, 1))
        grp.addRow(IntegerDescriptor(Inputs.SIGNAL_PERIOD, "Signal Period", 9, 1, 9999, 1))
        tab = sd.addTab("Display")
        // Allow the user to configure the settings for the paths and the histogram
        grp = tab.addGroup("Paths")
        grp.addRow(PathDescriptor(Inputs.PATH, "MACD Path", defaults.lineColor, 1.5f, null, true, false, true))
        grp.addRow(PathDescriptor(Inputs.SIGNAL_PATH, "Signal Path", defaults.red, 1.0f, null, true, false, true))
        grp.addRow(BarDescriptor(Inputs.BAR, "Bar Color", defaults.barColor, true, true))
        // Allow the user to display and configure indicators on the vertical axis
        grp = tab.addGroup("Indicators")
        grp.addRow(IndicatorDescriptor(Inputs.IND, "MACD Ind", null, null, false, true, true))
        grp.addRow(IndicatorDescriptor(Inputs.SIGNAL_IND, "Signal Ind", defaults.red, null, false, false, true))
        grp.addRow(IndicatorDescriptor(HIST_IND, "Hist Ind", defaults.barColor, null, false, false, true))
        val desc = createRD()
        desc.minTick = 0.0001
        desc.setLabelSettings(Inputs.INPUT, Inputs.PERIOD, Inputs.PERIOD2, Inputs.SIGNAL_PERIOD)
        // We are exporting 3 values: MACD, SIGNAL and HIST (histogram)
        desc.exportValue(ValueDescriptor(Values.MACD, "MACD", arrayOf(Inputs.INPUT, Inputs.PERIOD, Inputs.PERIOD2)))
        desc.exportValue(ValueDescriptor(Values.SIGNAL, "MACD Signal", arrayOf(Inputs.SIGNAL_PERIOD)))
        desc.exportValue(
            ValueDescriptor(
                Values.HIST,
                "MACD Histogram",
                arrayOf(Inputs.PERIOD, Inputs.PERIOD2, Inputs.SIGNAL_PERIOD)
            )
        )
        // There are two paths, the MACD path and the Signal path
        desc.declarePath(Values.MACD, Inputs.PATH)
        desc.declarePath(Values.SIGNAL, Inputs.SIGNAL_PATH)
        // Bars displayed as the histogram
        desc.declareBars(Values.HIST, Inputs.BAR)
        // These are the indicators that are displayed in the vertical axis
        desc.declareIndicator(Values.MACD, Inputs.IND)
        desc.declareIndicator(Values.SIGNAL, Inputs.SIGNAL_IND)
        desc.declareIndicator(Values.HIST, HIST_IND)

        // These variables are used to define the range of the vertical axis
        desc.setRangeKeys(Values.MACD, Values.SIGNAL, Values.HIST)
        // Display a 'Zero' line that is dashed.
        desc.addHorizontalLine(LineInfo(0.0, null, 1.0f, floatArrayOf(3f, 3f)))
    }

    /** This method calculates the MACD values for the data at the given index.  */
    override fun calculate(index: Int, ctx: DataContext) {
        val period1 = settings.getInteger(Inputs.PERIOD)
        val period2 = settings.getInteger(Inputs.PERIOD2)
        val period = Util.max(period1, period2)
        if (index < period) return  // not enough data to compute the MAs

        // MACD is the difference between two moving averages.
        // In our case we are going to use an exponential moving average (EMA)
        val input = settings.getInput(Inputs.INPUT)
        val series = ctx.dataSeries
        val ma1 = series.ema(index, period1, input)
        val ma2 = series.ema(index, period2, input)
        if (ma1 == null || ma2 == null) return

        // Define the MACD value for this index
        val macd = ma1 - ma2
        //debug("Setting MACD value for index: " + index + " MACD: " + MACD);
        series.setDouble(index, Values.MACD, macd)
        val signalPeriod = settings.getInteger(Inputs.SIGNAL_PERIOD)
        if (index < period + signalPeriod) return  // Not enough data yet

        // Calculate moving average of MACD (signal path)
        val signal = series.sma(index, signalPeriod, Values.MACD)
        series.setDouble(index, Values.SIGNAL, signal)
        if (signal == null) return

        // Histogram is the difference between the MACD and the signal path
        series.setDouble(index, Values.HIST, macd - signal)
        series.setComplete(index)
    }

    companion object {
        const val HIST_IND = "histInd" // Histogram Parameter 
    }
}