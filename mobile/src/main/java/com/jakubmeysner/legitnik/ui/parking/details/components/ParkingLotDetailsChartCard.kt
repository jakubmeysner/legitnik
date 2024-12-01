package com.jakubmeysner.legitnik.ui.parking.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jakubmeysner.legitnik.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlin.math.ceil

@Composable
fun ParkingLotDetailsChartCard(
    freePlacesHistory: List<Pair<String, Int>>?,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            Text(
                text = stringResource(R.string.parking_lot_details_history),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (freePlacesHistory.isNullOrEmpty()) {
                ParkingLotDetailsDataUnavailable()
            } else {
                val modelProducer = remember(freePlacesHistory) { CartesianChartModelProducer() }
                val labelListKey = ExtraStore.Key<List<String>>()
                val (xs, ys) = freePlacesHistory.unzip()

                LaunchedEffect(freePlacesHistory) {
                    modelProducer.runTransaction {
                        lineSeries {
                            series(
                                y = ys
                            )
                        }
                        extras {
                            it[labelListKey] = xs
                        }
                    }
                }

                val surfaceColor = MaterialTheme.colorScheme.surfaceBright
                val lineColor = MaterialTheme.colorScheme.primary
                val marker = DefaultCartesianMarker(
                    label = TextComponent(margins = Dimensions(8f)),
                    indicator = { color ->
                        ShapeComponent(
                            fill = fill(surfaceColor),
                            shape = CorneredShape.Pill,
                            strokeFill = Fill(color),
                            strokeThicknessDp = 2f
                        )
                    },
                    indicatorSizeDp = 10f,
                )

                //idk how to calculate these to make whole chart visible without need to scroll horizontally
                val spacing = ceil(ys.size / 10f).toInt()
                val pointSpacing = ceil(ys.size / 20f).toInt()
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(lineProvider =
                        LineCartesianLayer.LineProvider.Companion.series(
                            LineCartesianLayer.rememberLine(
                                remember { LineCartesianLayer.LineFill.single(fill(lineColor)) }
                            )
                        ),
                            pointSpacing = pointSpacing.dp
                        ),
                        startAxis =
                        VerticalAxis.rememberStart(
                        ),
                        bottomAxis =
                        HorizontalAxis.rememberBottom(
                            valueFormatter = { context, x, _ ->
                                context.model.extraStore[labelListKey][x.toInt()]
                            },
                            labelRotationDegrees = 300f,
                            itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                                spacing = spacing,
                                addExtremeLabelPadding = false
                            )
                        ),
                        layerPadding = cartesianLayerPadding(scalableStart = 0.dp),
                        marker = marker,
                        persistentMarkers = rememberExtraLambda(marker) { marker at xs.size - 1 },
                    ),
                    modelProducer,
                )
            }
        }
    }

}
