package com.example.coretechv2.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coretechv2.dataclasses.APICallTables

/**
 * Displays a Gel Time test result in a structured row layout.
 *
 * This UI component shows:
 * - Test type label ("GelTime")
 * - Gel time value
 * - Catalyst used
 * - Catalyst percentage
 *
 * Used in:
 * - Assembly test result screens
 * - History or summary lists of gel time tests
 *
 * @param results Gel time test data from [APICallTables.gelTimeTest]
 */
@Composable
fun GelTimeCard(results: APICallTables.gelTimeTest) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Min)
            .padding(7.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("GelTime", style = MaterialTheme.typography.titleSmall)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Time: " + results.GELTIME)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Catalyst: " + results.GELCAT)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Percentage: " + results.GELCATPERCENT + "%")
        }
    }
}

/**
 * Displays a peak exotherm test result in a structured row layout.
 *
 * This component shows:
 * - Peak exotherm test type
 * - Time taken to reach the peak
 * - Peak temperature
 * - Catalyst used and its percentage
 *
 * @param results Peak exotherm test data from [APICallTables.peakExothermTest].
 */
@Composable
fun PeakExothermCard(results: APICallTables.peakExothermTest) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Min)
            .padding(7.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Peak Exo", style = MaterialTheme.typography.titleSmall)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Time: " + results.GELTIME)
        }
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Temp: " + results.PEAKTEMPERATURE + "°C")
        }
        Column(
            modifier = Modifier
                .weight(4f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Catalyst: " + results.GELCAT + " @ " + results.GELCATPERCENT + "%")
        }
    }
}

/**
 * Displays an elongational break test result in a structured row layout.
 *
 * This component shows:
 * - The number of days the test sample was set
 * - The measured elongation percentage
 *
 * @param results Elongational break test data from
 * [APICallTables.ElongationalBreakTest].
 */
@Composable
fun ElongationalBreakCard(results: APICallTables.ElongationalBreakTest) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Min)
            .padding(7.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Elongation", style = MaterialTheme.typography.titleSmall)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Days Set: " + results.DAYSSET)
        }
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Elongation: " + results.ELONGATIONPERCENT + "%")
        }
    }
}

/**
 * Displays a flammability test result in a structured row layout.
 *
 * This component shows:
 * - Flammability test time
 * - Burn length in millimetres
 * - Number of days the test sample was set
 *
 * @param results Flammability test data from [APICallTables.FlammabilityTest].
 */
@Composable
fun FlammabilityCard(results: APICallTables.FlammabilityTest) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Min)
            .padding(7.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Flame", style = MaterialTheme.typography.titleSmall)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Time: " + results.FLAMETIME)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Length: " + results.BURNLENGTH + "mm")
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Days Set: " + results.DAYSSET)
        }
    }
}

/**
 * Displays a resistivity test result in a structured row layout.
 *
 * This component shows:
 * - The number of days the test sample was set
 * - The measured electrical resistivity in ohms
 *
 * @param results Resistivity test data from [APICallTables.ResistivityTest].
 */
@Composable
fun ResistivityCard(results: APICallTables.ResistivityTest) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Min)
            .padding(7.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Resistivity", style = MaterialTheme.typography.titleSmall)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Days Set: " + results.DAYSSET)
        }
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Resistivity: " + results.RESISTIVITYOHM + " Ohm")
        }
    }
}


/**
 * Displays a viscosity test result in a structured tabular row layout.
 *
 * This component shows:
 * - Spindle type
 * - Index reading
 * - Multiple viscosity readings across different speeds
 *
 * It dynamically adjusts labels depending on spindle type:
 * - Spindle A–G uses higher range values (e.g. 100, 50, 20...)
 * - Other spindle types use lower range values (e.g. 60, 30, 12...)
 *
 * Used in:
 * - Viscosity test result screens
 * - Production quality inspection views
 *
 * @param results Viscosity test data from [APICallTables.viscosityTest]
 */
@Composable
fun ViscosityCard(results: APICallTables.viscosityTest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(7.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text("Viscosity", style = MaterialTheme.typography.titleSmall)
        }
        VerticalDivider(modifier = Modifier.fillMaxHeight())
        Spacer(Modifier.width(10.dp))
        Column(Modifier.width(75.dp)) {
            Text("Spindle:")
            Text("Index:")
        }
        Column(Modifier.width(40.dp), horizontalAlignment = Alignment.End) {
            Text(results.SPINDLE)
            Text(results.INDEXREADING.toString())
        }
        Spacer(Modifier.width(20.dp))
        Row() {
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "100"
                    } else {
                        "60"
                    }
                )
                HorizontalDivider()
                Text(results.READING60.cleanFormat())
            }
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "50"
                    } else {
                        "30"
                    }
                )
                HorizontalDivider()
                Text(results.READING30.cleanFormat())
            }
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "20"
                    } else {
                        "12"
                    }
                )
                HorizontalDivider()
                Text(results.READING12.cleanFormat())
            }
            VerticalDivider(modifier = Modifier.fillMaxHeight())
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "10"
                    } else {
                        "6"
                    }
                )
                HorizontalDivider()
                Text(results.READING6.cleanFormat())
            }
            VerticalDivider()
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "5"
                    } else {
                        "3"
                    }
                )
                HorizontalDivider()
                Text(results.READING3.cleanFormat())
            }
            VerticalDivider()
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "2.5"
                    } else {
                        "1.5"
                    }
                )
                HorizontalDivider()
                Text(results.READING1_5.cleanFormat())
            }
            VerticalDivider()
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "1"
                    } else {
                        "0.6"
                    }
                )
                HorizontalDivider()
                Text(results.READING0_6.cleanFormat())
            }
            VerticalDivider()
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (results.SPINDLE in listOf("A", "B", "C", "D", "E", "F", "G")) {
                        "0.5"
                    } else {
                        "0.3"
                    }
                )
                HorizontalDivider()
                Text(results.READING0_3.cleanFormat())
            }
            VerticalDivider()
        }

    }
}

/**
 * Displays an assembly adjustment entry in a compact row layout.
 *
 * This component shows:
 * - Line description
 * - Line code
 * - Adjusted quantity with unit
 *
 * Used in:
 * - Assembly order adjustment screens
 * - Production change tracking views
 *
 * @param results Adjustment data from [APICallTables.assemblyAdjustment]
 */
@Composable
fun AdjustmentCard(results: APICallTables.assemblyAdjustment) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Center
            ) {
                Text(results.LINEDESCRIPTION, style = MaterialTheme.typography.titleSmall)
                Text(results.LINECODE, style = MaterialTheme.typography.labelSmall)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End
            ) {
                Text("%.3f".format(results.ADJUSTQTY) + results.LINEUNIT)
            }
        }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
    }
}


/**
 * Extension function to format a Double into a cleaner string representation.
 *
 * Rules:
 * - If the number has no decimal component, it is displayed as an Int
 * - Otherwise, the full decimal value is preserved
 *
 * Examples:
 * - 12.0 → "12"
 * - 12.5 → "12.5"
 *
 * @return Clean string representation of the number
 */
fun Double.cleanFormat(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}