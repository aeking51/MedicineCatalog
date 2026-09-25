package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import com.example.data.model.UserRole
import com.example.ui.AppTab
import com.example.ui.AyurvedaViewModel
import com.example.ui.components.AyurBottomNav
import com.example.ui.components.AyurTopHeader
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AyurvedaMedicineDetailScreen
import com.example.ui.screens.AyurvedaSplashScreen
import com.example.ui.screens.CatalogueScreen
import com.example.ui.screens.HomeScreen
import com.example.data.local.ThemePreferences
import com.example.ui.screens.PrakritiProfileScreen
import com.example.ui.theme.AyurTheme
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import com.example.ui.components.AyurNavigationRail
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {

  private val viewModel: AyurvedaViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ThemePreferences.init(this)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      var showSplash by rememberSaveable { mutableStateOf(true) }

      MyApplicationTheme(
        themeMode = uiState.appThemeMode,
        motionProfile = uiState.glassMotionProfile
      ) {
        Crossfade(
          targetState = showSplash,
          animationSpec = tween(durationMillis = 350),
          label = "splash_transition"
        ) { splashVisible ->
          if (splashVisible) {
            AyurvedaSplashScreen(
              onTimeout = { showSplash = false }
            )
          } else {
            AyurvedaApp(viewModel = viewModel)
          }
        }
      }
    }
  }
}

@Composable
fun AyurvedaApp(
  viewModel: AyurvedaViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }
  val themeColors = AyurTheme.colors

  LaunchedEffect(uiState.snackbarMessage) {
    uiState.snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  // Show Auth Screen (Login / Signup / Forgot Password) if not authenticated
  if (!uiState.isAuthenticated) {
    AuthScreen(
      uiState = uiState,
      onSetAuthMode = { viewModel.setAuthMode(it) },
      onLogin = { email, pass -> viewModel.login(email, pass) },
      onQuickLoginAs = { viewModel.quickLoginAs(it) },
      onGuestLogin = { viewModel.loginAsGuest() },
      onSignup = { name, email, pass, role, prakriti, desig ->
        viewModel.signup(name, email, pass, role, prakriti, desig)
      },
      onRequestReset = { viewModel.requestPasswordReset(it) },
      onCompleteReset = { email, otp, newPass ->
        viewModel.completePasswordReset(email, otp, newPass)
      },
      onDismissError = { viewModel.clearAuthMessages() }
    )
    return
  }

  // Show dedicated Medicine Detail Screen (Full Window) when a medicine is selected
  uiState.selectedMedicine?.let { med ->
    AyurvedaMedicineDetailScreen(
      medicine = med,
      onNavigateBack = { viewModel.selectMedicine(null) },
      onUpdatePhoto = if (uiState.currentUser.role != UserRole.GUEST) {
        { newUrl -> viewModel.updateMedicinePhoto(med.id, newUrl) }
      } else null,
      modifier = modifier
    )
    return
  }

  Box(modifier = Modifier.fillMaxSize()) {
    if (themeColors.isGlass) {
      Image(
        painter = painterResource(id = R.drawable.img_ayurveda_glass_bg),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
      // Apple iOS Liquid Glass crystal dynamic illumination - non-faded, vibrant, high-refraction
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0x55000000), // Soft top crystal vignette for status bar and top header clarity
                Color(0x15000000), // Smooth fade
                Color(0x00FFFFFF), // Crystal clear center - botanical vibrancy fully preserved
                Color(0x25062615)  // Rich botanical depth for bottom navigation bar
              )
            )
          )
      )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val isTablet = maxWidth >= 600.dp

      Row(modifier = Modifier.fillMaxSize()) {
        if (isTablet) {
          AyurNavigationRail(
            currentTab = uiState.currentTab,
            currentUserRole = uiState.currentUser.role,
            onTabSelected = { viewModel.setTab(it) }
          )
        }

        Scaffold(
          modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .then(if (!themeColors.isGlass) Modifier.background(themeColors.background) else Modifier),
          containerColor = if (themeColors.isGlass) Color.Transparent else themeColors.background,
          snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
          topBar = {
            val isSearchVisible = uiState.currentTab != AppTab.PROFILE && uiState.currentTab != AppTab.ADMIN
            AyurTopHeader(
              userName = uiState.currentUser.name.split(" ").firstOrNull() ?: uiState.currentUser.name,
              userRole = uiState.currentUser.role,
              onProfileClick = { viewModel.setTab(AppTab.PROFILE) },
              showSearchBar = isSearchVisible,
              searchQuery = uiState.searchQuery,
              onSearchQueryChanged = { query ->
                viewModel.onSearchQueryChanged(query)
                if (query.isNotEmpty() && uiState.currentTab != AppTab.LIBRARY && uiState.currentTab != AppTab.ADMIN) {
                  viewModel.setTab(AppTab.LIBRARY)
                }
              },
              searchHistory = uiState.searchHistory,
              onSearchSubmitted = { query ->
                viewModel.performSearch(query)
                if (uiState.currentTab != AppTab.LIBRARY) {
                  viewModel.setTab(AppTab.LIBRARY)
                }
              },
              onRemoveSearchHistoryItem = { query ->
                viewModel.removeSearchQueryFromHistory(query)
              },
              onClearSearchHistory = {
                viewModel.clearSearchHistory()
              }
            )
          },
          bottomBar = {
            if (!isTablet) {
              AyurBottomNav(
                currentTab = uiState.currentTab,
                currentUserRole = uiState.currentUser.role,
                onTabSelected = { viewModel.setTab(it) }
              )
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1400.dp)
            ) {
              when (uiState.currentTab) {
        AppTab.HOME -> HomeScreen(
          uiState = uiState,
          onSelectMedicine = { viewModel.selectMedicine(it) },
          onCategorySelected = { cat ->
            viewModel.onCategorySelected(cat)
            viewModel.setTab(AppTab.LIBRARY)
          },
          onNavigateToLibrary = { viewModel.setTab(AppTab.LIBRARY) },
          onPromptSignIn = { viewModel.logout() }
        )

        AppTab.LIBRARY -> CatalogueScreen(
          uiState = uiState,
          onCategorySelected = { viewModel.onCategorySelected(it) },
          onDoshaSelected = { viewModel.onDoshaSelected(it) },
          onSelectMedicine = { viewModel.selectMedicine(it) },
          onRefresh = { viewModel.refreshCatalogue() },
          onSearchSubmitted = { query -> viewModel.performSearch(query) },
          onRemoveSearchHistoryItem = { query -> viewModel.removeSearchQueryFromHistory(query) },
          onClearSearchHistory = { viewModel.clearSearchHistory() }
        )

        AppTab.INSIGHTS -> {
          // Health tracking tab removed; default to Catalogue
          CatalogueScreen(
            uiState = uiState,
            onCategorySelected = { viewModel.onCategorySelected(it) },
            onDoshaSelected = { viewModel.onDoshaSelected(it) },
            onSelectMedicine = { viewModel.selectMedicine(it) },
            onRefresh = { viewModel.refreshCatalogue() },
            onSearchSubmitted = { query -> viewModel.performSearch(query) },
            onRemoveSearchHistoryItem = { query -> viewModel.removeSearchQueryFromHistory(query) },
            onClearSearchHistory = { viewModel.clearSearchHistory() }
          )
        }

        AppTab.PROFILE -> PrakritiProfileScreen(
          uiState = uiState,
          onNavigateToAdmin = { viewModel.setTab(AppTab.ADMIN) },
          onLogout = { viewModel.logout() },
          onUpdateProfile = { name, desig, phone, notes ->
            viewModel.updateCurrentUserProfile(name, desig, phone, notes)
          },
          onChangePassword = { currentPass, newPass, confirmPass, callback ->
            viewModel.changeCurrentUserPassword(currentPass, newPass, confirmPass, callback)
          },
          onThemeSelected = { viewModel.setAppThemeMode(it) }
        )

        AppTab.ADMIN -> AdminDashboardScreen(
          uiState = uiState,
          onUpdateUserRole = { id, role -> viewModel.updateUserRole(id, role) },
          onUpdateUserStatus = { id, status -> viewModel.updateUserStatus(id, status) },
          onAddNewUser = { viewModel.addNewUser(it) },
          onSetUserRoleFilter = { viewModel.setUserRoleFilter(it) },
          onSelectUserForDetail = { viewModel.selectUserForDetail(it) },
          onOpenAddMedicineDialog = { viewModel.setAddMedicineDialogOpen(it) },
          onOpenAddUserDialog = { viewModel.setAddUserDialogOpen(it) },
          onAddNewMedicine = { viewModel.addNewMedicine(it) },
          onUpdateStock = { id, stock -> viewModel.updateMedicineStock(id, stock) },
          onToggleVitality = { viewModel.toggleMedicineVitality(it) },
          onDeleteMedicine = { viewModel.deleteMedicine(it) },
          onSelectMedicine = { viewModel.selectMedicine(it) },
          onPrescribeToPatient = { med, patientId -> viewModel.prescribeMedicineToPatient(med, patientId) }
        )
      }
    }
  }
}
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

