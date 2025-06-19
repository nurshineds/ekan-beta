@file:OptIn(ExperimentalMaterial3Api::class)

package com.kel5.ekanbeta.Screen.UserScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SettingsInputComponent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kel5.ekanbeta.Common.formatRupiah
import com.kel5.ekanbeta.Data.ProductData
import com.kel5.ekanbeta.ViewModel.ProductViewModel
import com.kel5.ekanbeta.ui.theme.BackgroundColor
import com.kel5.ekanbeta.ui.theme.Poppins
import com.kel5.ekanbeta.ui.theme.PrimaryColor
import coil.compose.AsyncImage
import com.kel5.ekanbeta.Data.CategoryData
import kotlinx.coroutines.launch

@Composable
fun ProductUserScreen(navController: NavHostController) {
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.productList.collectAsState()
    val categories by productViewModel.categoryList.collectAsState()

    val searchText = remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val selectedCategoryInput = remember { mutableStateOf<String?>(null) }
    val priceMinInput = remember { mutableStateOf("") }
    val priceMaxInput = remember { mutableStateOf("") }

    val selectedCategory = remember { mutableStateOf<String?>(null) }
    val priceMin = remember { mutableStateOf("") }
    val priceMax = remember { mutableStateOf("") }

    val filteredProducts by remember(
        products,
        searchText.value,
        selectedCategory.value,
        priceMin.value,
        priceMax.value
    ) {
        derivedStateOf {
            val min = priceMin.value.toIntOrNull() ?: Int.MIN_VALUE
            val max = priceMax.value.toIntOrNull() ?: Int.MAX_VALUE

            products.filter { prod ->
                prod.nama.contains(searchText.value, ignoreCase = true) &&
                        (selectedCategory.value == null || prod.kategori == selectedCategory.value) &&
                        prod.hargaDiskon in min..max
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                FilterSidebar(
                    selectedCategory = selectedCategoryInput,
                    priceMin = priceMinInput,
                    priceMax = priceMaxInput,
                    categories = categories,
                    onApply = {
                        selectedCategory.value = selectedCategoryInput.value
                        priceMin.value = priceMinInput.value
                        priceMax.value = priceMaxInput.value
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = { UserBottomBar(navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(PrimaryColor)
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Katalog",
                        fontSize = 24.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Discover",
                        fontSize = 21.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    TextField(
                        value = searchText.value,
                        onValueChange = { searchText.value = it },
                        placeholder = {
                            Text(
                                text = "Cari...",
                                fontSize = 16.sp,
                                fontFamily = Poppins,
                                color = Color.White
                            )
                        },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0x802EADC9),
                            unfocusedContainerColor = Color(0x802EADC9),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular",
                            fontSize = 21.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Button(
                            onClick = {
                                selectedCategoryInput.value = selectedCategory.value
                                priceMinInput.value = priceMin.value
                                priceMaxInput.value = priceMax.value
                                scope.launch { drawerState.open() }
                            },
                            modifier = Modifier
                                .height(26.dp)
                                .width(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(3.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = PrimaryColor
                            ),
                            border = BorderStroke(1.dp, PrimaryColor)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SettingsInputComponent,
                                contentDescription = "Filter Produk",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            rowItems.forEach { prod ->
                                ProductCard(
                                    product = prod,
                                    modifier = Modifier.weight(1f),
                                    navController = navController
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSidebar(
    selectedCategory: MutableState<String?>,
    priceMin: MutableState<String>,
    priceMax: MutableState<String>,
    categories: List<CategoryData>,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Text(
            text = "Filter",
            fontSize = 21.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Kategori", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = Poppins)

        Spacer(modifier = Modifier.height(8.dp))

        categories.sortedBy { it.nama.lowercase() }.forEach { cat ->
            val isSelected = selectedCategory.value == cat.id
            Button(
                onClick = {
                    selectedCategory.value = if (isSelected) null else cat.id
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) PrimaryColor else Color.Transparent,
                    contentColor = if (isSelected) Color.White else Color.Black
                ),
                border = if (!isSelected) BorderStroke(1.dp, Color.Gray) else null,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(cat.nama, fontFamily = Poppins)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Harga (Rp)", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Poppins)

        Spacer(modifier = Modifier.height(8.dp))
        val borderColor = PrimaryColor

        TextField(
            value = priceMin.value,
            onValueChange = {
                if (it.all { ch -> ch.isDigit() } || it.isEmpty()) {
                    priceMin.value = it
                }
            },
            label = { Text("Min", fontFamily = Poppins) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = borderColor,
                unfocusedIndicatorColor = borderColor,
                disabledIndicatorColor = borderColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = priceMax.value,
            onValueChange = {
                if (it.all { ch -> ch.isDigit() } || it.isEmpty()) {
                    priceMax.value = it
                }
            },
            label = { Text("Max", fontFamily = Poppins) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = borderColor,
                unfocusedIndicatorColor = borderColor,
                disabledIndicatorColor = borderColor
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text("Terapkan Filter", fontFamily = Poppins, color = Color.White)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                selectedCategory.value = null
                priceMin.value = ""
                priceMax.value = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Hapus Filter", fontFamily = Poppins, color = Color.DarkGray)
        }
    }
}

@Composable
fun ProductCard(product: ProductData, modifier: Modifier = Modifier, navController: NavHostController) {
    Card(
        modifier = modifier
            .width(150.dp)
            .height(200.dp)
            .clickable {
                navController.navigate("ProductDetail/${product.id}")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.nama,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.nama,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                fontFamily = Poppins,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = formatRupiah(product.hargaDiskon) + "/kg",
                color = Color.Gray,
                fontSize = 12.sp,
                fontFamily = Poppins,
                textAlign = TextAlign.Center
            )
        }
    }
}
