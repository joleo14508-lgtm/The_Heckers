package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InspectionRecord
import com.example.data.model.OnionGrade
import com.example.ui.components.GradeBadge
import com.example.ui.theme.AgrigradeBackground
import com.example.ui.theme.ButtonGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HeadingsLogo
import com.example.ui.theme.LightLayer
import com.example.ui.theme.SagePanel
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun BatchHistoryScreen(
    inspections: List<InspectionRecord>,
    onSelectInspection: (InspectionRecord) -> Unit,
    onDeleteInspection: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterGrade by remember { mutableStateOf<OnionGrade?>(null) }

    val filteredList = inspections.filter { record ->
        val matchesQuery = searchQuery.isBlank() ||
                record.reportId.contains(searchQuery, ignoreCase = true) ||
                record.lotNumber.contains(searchQuery, ignoreCase = true) ||
                record.variety.contains(searchQuery, ignoreCase = true)
        val matchesGrade = selectedFilterGrade == null || record.overallGrade == selectedFilterGrade
        matchesQuery && matchesGrade
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgrigradeBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = HeadingsLogo
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Procurement Lots & History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HeadingsLogo
                )
                Text(
                    text = "${inspections.size} total specimen inspections logged",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Lot #, Report ID, or variety...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LightLayer,
                unfocusedContainerColor = LightLayer,
                focusedBorderColor = ButtonGreen,
                unfocusedBorderColor = CardBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Grade Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedFilterGrade == null,
                    onClick = { selectedFilterGrade = null },
                    label = { Text("All Grades", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonGreen,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilterGrade == OnionGrade.GRADE_A,
                    onClick = { selectedFilterGrade = if (selectedFilterGrade == OnionGrade.GRADE_A) null else OnionGrade.GRADE_A },
                    label = { Text("Grade A", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonGreen,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilterGrade == OnionGrade.GRADE_B,
                    onClick = { selectedFilterGrade = if (selectedFilterGrade == OnionGrade.GRADE_B) null else OnionGrade.GRADE_B },
                    label = { Text("Grade B", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonGreen,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilterGrade == OnionGrade.REJECT,
                    onClick = { selectedFilterGrade = if (selectedFilterGrade == OnionGrade.REJECT) null else OnionGrade.REJECT },
                    label = { Text("Reject", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonGreen,
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No inspection records match filter criteria.",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    Card(
                        onClick = { onSelectInspection(item) },
                        colors = CardDefaults.cardColors(containerColor = LightLayer),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth().testTag("history_item_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.reportId,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = HeadingsLogo
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    GradeBadge(grade = item.overallGrade)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${item.lotNumber} • ${item.variety}",
                                    fontSize = 12.sp,
                                    color = TextDark
                                )
                                Text(
                                    text = "⌀${item.diameterMm}mm • ₹${item.recommendedPricePerKg}/kg • ${item.formattedDate()}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDeleteInspection(item.id) },
                                    modifier = Modifier.testTag("delete_item_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = TextMuted.copy(alpha = 0.7f)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "View",
                                    tint = ButtonGreen
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
