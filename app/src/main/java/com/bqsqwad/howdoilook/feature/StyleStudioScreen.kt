package com.bqsqwad.howdoilook.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

private data class StyleTool(
    val title: String,
    val description: String,
    val prompt: String,
    val hint: String
)

private data class SavedStyleEntry(
    val id: String,
    val tool: String,
    val value: String,
    val createdAt: Long
)

private val styleTools = listOf(
    StyleTool("Outfit calendar", "Plan clothes for a date or event.", "What are you planning to wear, and for which date or occasion?", "Date, event and outfit"),
    StyleTool("Weather-aware outfit notes", "Record weather considerations without tracking your location.", "What weather do you expect, and what clothing will keep you comfortable?", "Weather and clothing notes"),
    StyleTool("Capsule wardrobe builder", "Build combinations from clothes you already own.", "List a few garments you own that you want to combine.", "Example: navy trousers, white shirt, denim jacket"),
    StyleTool("Wardrobe gap finder", "Record practical wardrobe needs before buying anything.", "What clothing item would make your existing outfits more versatile?", "Item and why it is useful"),
    StyleTool("Outfit rotation tracker", "Keep a simple record of outfits you have worn.", "What did you wear today, and where did you wear it?", "Outfit and occasion"),
    StyleTool("Laundry and care reminders", "Track washing, ironing and care tasks.", "Which garment needs care, and what task should you remember?", "Garment and care task"),
    StyleTool("Clothing care guide", "Save fabric and clothing-care instructions.", "Enter the garment fabric or care-label symbols you want to understand.", "Fabric or care-label symbols"),
    StyleTool("Packing checklist", "Prepare a travel list for a trip.", "Enter the destination, trip length and items to pack.", "Trip details and packing list"),
    StyleTool("Dress-code explainer", "Save questions about event dress codes.", "What dress code or event invitation would you like clarified?", "Dress code or event"),
    StyleTool("Interview outfit checklist", "Prepare a clean, comfortable interview outfit.", "List the outfit pieces and preparation tasks for your interview.", "Outfit and preparation checklist"),
    StyleTool("Event outfit planner", "Plan clothing and essentials for an event.", "What is the event, and what outfit and essentials will you prepare?", "Event and outfit plan"),
    StyleTool("Budget-aware wardrobe planner", "Prioritize needs and keep a clothing budget.", "Enter your budget and the practical clothing needs to prioritize.", "Budget and priorities"),
    StyleTool("Cost-per-wear tracker", "Track garment use and purchase cost.", "Enter the garment, its cost and how many times you have worn it.", "Garment, cost and wears"),
    StyleTool("Color palette notebook", "Save clothing colors and combinations.", "Enter colors you want to remember or combine.", "Example: cobalt, white and charcoal"),
    StyleTool("Contrast and accessibility checker", "Note clothing contrast for visibility and readability.", "Describe the colors and where visibility or contrast matters.", "Colors and context"),
    StyleTool("Tailoring and repair notes", "Track alterations and small repairs.", "Which garment needs a repair or alteration?", "Garment and repair"),
    StyleTool("Closet inventory search", "Create searchable notes about garments you own.", "Enter garment type, color, season and occasion.", "Garment details"),
    StyleTool("Offline style boards", "Group outfit ideas into named boards on this device.", "Enter a board name and the outfit ideas to save.", "Board name and ideas"),
    StyleTool("Private data dashboard", "Review, export or clear this local style notebook.", "Your saved style notes are stored locally on this device. Use Export or Clear below.", "No entry required"),
    StyleTool("Camera and photo quality coach", "Keep a checklist for clearer, more useful photos.", "What should you check before taking a photo? Consider light, focus, background and framing.", "Photo checklist")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleStudioScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("style_studio", 0) }
    val entries = remember {
        mutableStateListOf<SavedStyleEntry>().apply {
            val raw = prefs.getString("entries", "[]") ?: "[]"
            runCatching {
                val array = JSONArray(raw)
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    add(SavedStyleEntry(
                        id = item.optString("id"),
                        tool = item.optString("tool"),
                        value = item.optString("value"),
                        createdAt = item.optLong("createdAt")
                    ))
                }
            }
        }
    }
    var selectedTool by remember { mutableStateOf<StyleTool?>(null) }
    var draft by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showDataInfo by remember { mutableStateOf(false) }

    fun persist() {
        val array = JSONArray()
        entries.forEach { entry ->
            array.put(JSONObject().put("id", entry.id).put("tool", entry.tool)
                .put("value", entry.value).put("createdAt", entry.createdAt))
        }
        prefs.edit().putString("entries", array.toString()).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Style Studio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDataInfo = true }) {
                        Icon(Icons.Default.Checkroom, contentDescription = "Local data information")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("A more useful wardrobe, one small plan at a time", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("20 practical tools for planning outfits, caring for clothes and taking clearer photos. Notes stay on this device.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Your tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${styleTools.size} tools", color = MaterialTheme.colorScheme.primary)
                }
            }
            items(styleTools, key = { it.title }) { tool ->
                Card(
                    onClick = {
                        selectedTool = tool
                        draft = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(tool.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(3.dp))
                            Text(tool.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.Add, contentDescription = "Open ${tool.title}", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            item {
                Text("Saved notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Search saved notes") }
                )
                if (entries.isEmpty()) {
                    Text("Your saved plans and checklists will appear here.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(
                entries.filter {
                    searchQuery.isBlank() ||
                        it.tool.contains(searchQuery, ignoreCase = true) ||
                        it.value.contains(searchQuery, ignoreCase = true)
                }.asReversed(),
                key = { it.id }
            ) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(entry.tool, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(entry.value, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = {
                            entries.removeAll { it.id == entry.id }
                            persist()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete saved note")
                        }
                    }
                }
            }
        }
    }

    selectedTool?.let { tool ->
        AlertDialog(
            onDismissRequest = { selectedTool = null },
            title = { Text(tool.title) },
            text = {
                Column {
                    Text(tool.prompt, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(10.dp))
                    if (tool.title == "Private data dashboard") {
                        Text("Saved notes: ${entries.size}", style = MaterialTheme.typography.titleSmall)
                        Text("Export creates a text copy using Android's share sheet. Clear removes all Style Studio notes from this device.")
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            val export = entries.joinToString("\n\n") { "${it.tool}\n${it.value}" }
                            val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, export.ifBlank { "No saved style notes." })
                            }
                            context.startActivity(android.content.Intent.createChooser(send, "Export style notes"))
                        }) { Text("Export notes") }
                        TextButton(onClick = {
                            entries.clear()
                            persist()
                            selectedTool = null
                        }) { Text("Clear all notes") }
                    } else {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                            placeholder = { Text(tool.hint) }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tool.title != "Private data dashboard" && draft.isNotBlank()) {
                            entries.add(SavedStyleEntry(
                                id = "${System.currentTimeMillis()}-${entries.size}",
                                tool = tool.title,
                                value = draft.trim(),
                                createdAt = System.currentTimeMillis()
                            ))
                            persist()
                        }
                        selectedTool = null
                    },
                    enabled = tool.title == "Private data dashboard" || draft.isNotBlank()
                ) { Text(if (tool.title == "Private data dashboard") "Done" else "Save locally") }
            },
            dismissButton = {
                TextButton(onClick = { selectedTool = null }) { Text("Cancel") }
            }
        )
    }

    if (showDataInfo) {
        AlertDialog(
            onDismissRequest = { showDataInfo = false },
            title = { Text("Your data stays on this device") },
            text = { Text("Style Studio saves notes in the app's private local storage. They are not uploaded by this feature. Export or clear them from Private data dashboard.") },
            confirmButton = { TextButton(onClick = { showDataInfo = false }) { Text("Got it") } }
        )
    }
}
