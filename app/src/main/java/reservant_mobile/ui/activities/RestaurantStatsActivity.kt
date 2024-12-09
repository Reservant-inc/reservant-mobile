package reservant_mobile.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MessageSheet
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.viewmodels.RestaurantStatsViewmodel

@Composable
fun RestaurantStatsActivity(onReturnClick: () -> Unit){
    val statsVM = viewModel<RestaurantStatsViewmodel>()

    val selectedYear = remember { mutableStateOf("2022") }
    val selectedMonth = remember { mutableStateOf("Jan") }
    val showYearDialog = remember { mutableStateOf(false) }
    val showStatsFilter = remember { mutableStateOf(false) }

    // List of years and months
    // todo: make it dynamic
    val years = listOf("2022", "2021", "2020", "2019")
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.People,
            text = stringResource(R.string.label_restaurant_stats),
            showBackButton = true,
            onReturnClick = onReturnClick
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable { showYearDialog.value = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = selectedYear.value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "year arrow",
                )

            }
            Icon(
                modifier = Modifier
                    .clickable{ showStatsFilter.value = true },
                imageVector = Icons.Default.FilterList,
                contentDescription = "stats filter",
            )
        }


        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            months.forEach { month ->
                FilterChip(
                    selected = selectedMonth.value == month,
                    onClick = { selectedMonth.value = month },
                    label = {
                        Text(
                            text = month,
                        )
                    }
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if(!statsVM.isLoading && !statsVM.loadingError){

            val data = listOf(4f, 12f, 8f, 16f, 21f, 3f,11f, 42f, 2f,3f,12f,50f,1f,2f,8f,23f)

            ChartCard(
                label = "Customers",
                data = data
            )
            Spacer(modifier = Modifier.height(24.dp))

            ChartCard(
                label = "New Customers",
                data = data,
                columnChart = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            ChartCard(
                label = "Income",
                data = data
            )
        }
        else if(statsVM.loadingError){
            MissingPage()
        }
        else{
            CircularProgressIndicator()
        }
    }

    if (showYearDialog.value) {
        Dialog(onDismissRequest = { showYearDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    years.forEach { year ->
                        Text(
                            text = year,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    selectedYear.value = year
                                    showYearDialog.value = false
                                },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if(showStatsFilter.value){
        MessageSheet(
            height = 450.dp,
            buttonLabelId = R.string.label_apply,
            onDismiss = {showStatsFilter.value = false},
            buttonOnClick = {

            },
            content = {}
        )
    }
}

@Composable
fun ChartCard(
    label: String,
    data:  List<Float>,
    columnChart: Boolean = false
){
    val showChartDialog = remember { mutableStateOf(false) }
    val values = entryModelOf(data.mapIndexed { index, item ->
        FloatEntry(
            x = index.toFloat(),
            y = item
        )
    })

    val chartComponent = @Composable{
        Chart(
            chart = if(columnChart) columnChart() else lineChart(),
            model = values,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        )
    }
    val labelComponent = @Composable{
        Text(
            modifier = Modifier.padding(10.dp),
            text = label,
            fontSize = 20.sp,
        )
    }

    Card(onClick = { showChartDialog.value = true }) {
        Column {
            labelComponent()
            chartComponent()
        }

    }
    if (showChartDialog.value) {
        Dialog(onDismissRequest = { showChartDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    labelComponent()
                    chartComponent()
                    Spacer(modifier = Modifier.height(24.dp))
                    TableScreen(data.mapIndexed { index, item ->
                        listOf(index, item)
                    })

                }
            }
        }
    }
}

// todo: make more universal
@Composable
fun TableScreen(
    tableData: List<List<Any>>
) {

    val equalWeight = 1f/tableData[0].size
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        item {
            Row(Modifier.background(MaterialTheme.colorScheme.primary)) {
                TableCell(
                    text = "Column 1",
                    textColor = MaterialTheme.colorScheme.background,
                    weight = equalWeight
                )
                TableCell(
                    text = "Column 2",
                    textColor = MaterialTheme.colorScheme.background,
                    weight = equalWeight
                )
            }
        }
        items(tableData.size) {
            val item = tableData[it]
            Row(Modifier.fillMaxWidth()) {
                item.forEach { cellVal ->
                    TableCell(text = cellVal.toString(), weight = equalWeight)
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline
            )
            .weight(weight)
            .padding(8.dp),
        style = textStyle,
        color = textColor
    )
}