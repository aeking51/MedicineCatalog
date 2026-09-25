package com.example

import com.example.data.model.AppUser
import com.example.data.model.DoshaType
import com.example.data.model.UserRole
import com.example.data.model.UserStatus
import com.example.ui.AyurvedaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testDefaultUsersExistWithDistinctRoles() {
    val vm = AyurvedaViewModel()
    val state = vm.uiState.value

    assertTrue(state.allUsers.isNotEmpty())
    val roles = state.allUsers.map { it.role }.toSet()
    assertTrue(roles.contains(UserRole.ADMIN))
    assertTrue(roles.contains(UserRole.PRACTITIONER))
    assertTrue(roles.contains(UserRole.PATIENT))
  }

  @Test
  fun testSwitchUserChangesActiveRole() {
    val vm = AyurvedaViewModel()
    val practitioner = vm.uiState.value.allUsers.first { it.role == UserRole.PRACTITIONER }

    vm.switchUser(practitioner)

    assertEquals(practitioner.id, vm.uiState.value.currentUser.id)
    assertEquals(UserRole.PRACTITIONER, vm.uiState.value.currentUser.role)
    assertTrue(vm.uiState.value.auditLogs.any { it.actionType == "USER_SWITCH" })
  }

  @Test
  fun testUpdateStockGeneratesAuditLog() {
    val vm = AyurvedaViewModel()
    val med = vm.uiState.value.allMedicines.first()
    val initialStock = med.stockUnits

    vm.updateMedicineStock(med.id, initialStock + 20)

    val updatedMed = vm.uiState.value.allMedicines.first { it.id == med.id }
    assertEquals(initialStock + 20, updatedMed.stockUnits)
    assertTrue(vm.uiState.value.auditLogs.any { it.actionType == "STOCK_UPDATE" })
  }

  @Test
  fun testUpdateUserRole() {
    val vm = AyurvedaViewModel()
    val patient = vm.uiState.value.allUsers.first { it.role == UserRole.PATIENT }

    vm.updateUserRole(patient.id, UserRole.PRACTITIONER)

    val updated = vm.uiState.value.allUsers.first { it.id == patient.id }
    assertEquals(UserRole.PRACTITIONER, updated.role)
    assertTrue(vm.uiState.value.auditLogs.any { it.actionType == "ROLE_CHANGE" || it.actionType == "ROLE_UPDATE" })
  }

  @Test
  fun testLoginWithValidCredentials() {
    val vm = AyurvedaViewModel()
    val patient = vm.uiState.value.allUsers.first { it.role == UserRole.PATIENT }

    val success = vm.login(patient.email, patient.password)
    assertTrue(success)
    assertTrue(vm.uiState.value.isAuthenticated)
    assertEquals(patient.id, vm.uiState.value.currentUser.id)
    assertTrue(vm.uiState.value.auditLogs.any { it.actionType == "LOGIN_SUCCESS" })
  }

  @Test
  fun testLoginWithInvalidPasswordFails() {
    val vm = AyurvedaViewModel()
    val patient = vm.uiState.value.allUsers.first { it.role == UserRole.PATIENT }

    val success = vm.login(patient.email, "wrong_password_xyz")
    org.junit.Assert.assertFalse(success)
    assertNotNull(vm.uiState.value.authErrorMessage)
  }

  @Test
  fun testSignupCreatesNewUserAndAuthenticates() {
    val vm = AyurvedaViewModel()
    val initialUserCount = vm.uiState.value.allUsers.size

    val success = vm.signup(
      name = "Devika Sharma",
      email = "devika.s@ayurveda.in",
      pass = "healthy123",
      role = UserRole.PATIENT,
      prakriti = DoshaType.VATA,
      designation = "Yoga Practitioner"
    )

    assertTrue(success)
    assertTrue(vm.uiState.value.isAuthenticated)
    assertEquals(initialUserCount + 1, vm.uiState.value.allUsers.size)
    assertEquals("Devika Sharma", vm.uiState.value.currentUser.name)
    assertEquals(DoshaType.VATA, vm.uiState.value.currentUser.prakriti)
    assertTrue(vm.uiState.value.auditLogs.any { it.actionType == "USER_REGISTERED" })
  }

  @Test
  fun testForgotPasswordOtpGenerationAndReset() {
    val vm = AyurvedaViewModel()
    val user = vm.uiState.value.allUsers.first()

    val requestSuccess = vm.requestPasswordReset(user.email)
    assertTrue(requestSuccess)
    assertEquals(com.example.ui.AuthMode.OTP_RESET, vm.uiState.value.authMode)
    val otp = vm.uiState.value.generatedOtpCode
    assertNotNull(otp)
    assertEquals(6, otp!!.length)

    // Complete reset with valid OTP
    val resetSuccess = vm.completePasswordReset(user.email, otp, "brandNewPass789")
    assertTrue(resetSuccess)
    assertEquals(com.example.ui.AuthMode.LOGIN, vm.uiState.value.authMode)

    // Verify user can now log in with the new password
    val loginWithNewPass = vm.login(user.email, "brandNewPass789")
    assertTrue(loginWithNewPass)
  }

  @Test
  fun testLogoutSetsUnauthenticated() {
    val vm = AyurvedaViewModel()
    assertTrue(vm.uiState.value.isAuthenticated)

    vm.logout()

    org.junit.Assert.assertFalse(vm.uiState.value.isAuthenticated)
    assertEquals(com.example.ui.AuthMode.LOGIN, vm.uiState.value.authMode)
    assertTrue(vm.uiState.value.auditLogs.any { it.actionType == "LOGOUT" })
  }

  @Test
  fun testCatalogueDatabaseLoadingFlow() {
    val vm = AyurvedaViewModel()
    // Initial fetch triggers loading
    vm.refreshCatalogue()
    assertTrue(vm.uiState.value.isCatalogueLoading)
  }

  @Test
  fun testPrunedUserListContainsExactlyRequiredUsers() {
    val vm = AyurvedaViewModel()
    val users = vm.uiState.value.allUsers
    assertEquals(3, users.size)

    val admin = users.firstOrNull { it.role == UserRole.ADMIN }
    assertNotNull(admin)
    assertTrue(admin!!.name.contains("Jerin", ignoreCase = true))

    val doctor = users.firstOrNull { it.role == UserRole.PRACTITIONER }
    assertNotNull(doctor)
    assertTrue(doctor!!.name.contains("Meera", ignoreCase = true))

    val patient = users.firstOrNull { it.role == UserRole.PATIENT }
    assertNotNull(patient)
    assertTrue(patient!!.name.contains("Arjun", ignoreCase = true))
  }

  @Test
  fun testNonAdminCannotElevateUserToAdminRole() {
    val vm = AyurvedaViewModel()
    // Switch to non-admin practitioner
    val doctor = vm.uiState.value.allUsers.first { it.role == UserRole.PRACTITIONER }
    vm.switchUser(doctor)

    val patient = vm.uiState.value.allUsers.first { it.role == UserRole.PATIENT }
    vm.updateUserRole(patient.id, UserRole.ADMIN)

    val patientAfter = vm.uiState.value.allUsers.first { it.id == patient.id }
    assertEquals(UserRole.PATIENT, patientAfter.role)
    assertTrue(vm.uiState.value.snackbarMessage?.contains("Only an Administrator") == true)
  }

  @Test
  fun testAdminCanElevateUserToAdminRole() {
    val vm = AyurvedaViewModel()
    // Jerin MR is the initial logged-in Admin
    val patient = vm.uiState.value.allUsers.first { it.role == UserRole.PATIENT }
    vm.updateUserRole(patient.id, UserRole.ADMIN)

    val patientAfter = vm.uiState.value.allUsers.first { it.id == patient.id }
    assertEquals(UserRole.ADMIN, patientAfter.role)
  }

  @Test
  fun testUserDirectoryCrudOperations() {
    val vm = AyurvedaViewModel()
    val initialCount = vm.uiState.value.allUsers.size

    // Create user
    val newUser = AppUser(
      id = "user_test_crud",
      name = "Sunil Verma",
      email = "sunil.v@example.com",
      phone = "+91 91234 56789",
      role = UserRole.PATIENT,
      prakriti = DoshaType.KAPHA,
      status = UserStatus.ACTIVE,
      designation = "Ayurvedic Patient",
      clinicalNotes = "Kapha congestion history",
      registeredDate = "Today",
      lastActive = "Now"
    )
    vm.addNewUser(newUser)
    assertEquals(initialCount + 1, vm.uiState.value.allUsers.size)

    // Update user
    val updated = newUser.copy(name = "Sunil Verma (Updated)", phone = "+91 99999 88888")
    vm.updateUser(updated)
    val fetched = vm.uiState.value.allUsers.first { it.id == newUser.id }
    assertEquals("Sunil Verma (Updated)", fetched.name)
    assertEquals("+91 99999 88888", fetched.phone)

    // Search user
    vm.onUserSearchQueryChanged("sunil")
    assertEquals("sunil", vm.uiState.value.userSearchQuery)

    // Delete user
    vm.deleteUser(newUser.id)
    assertEquals(initialCount, vm.uiState.value.allUsers.size)

    // Root admin deletion attempt should fail and leave count intact
    val admin = vm.uiState.value.allUsers.first { it.role == UserRole.ADMIN }
    vm.deleteUser(admin.id)
    assertEquals(initialCount, vm.uiState.value.allUsers.size)
    assertTrue(vm.uiState.value.allUsers.any { it.id == admin.id })
  }

  @Test
  fun testAyurvedaMedicineViewModelDataArchitecture() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
    val medVm = com.example.ui.AyurvedaMedicineViewModel(context)

    val ingredients = listOf(
      com.example.data.model.AyurvedaIngredient(
        name = "Haritaki",
        botanicalName = "Terminalia chebula",
        classicalRole = "Vata-hara and gentle laxative"
      ),
      com.example.data.model.AyurvedaIngredient(
        name = "Bibhitaki",
        botanicalName = "Terminalia bellirica",
        classicalRole = "Kapha-pitta shamaka"
      ),
      com.example.data.model.AyurvedaIngredient(
        name = "Amalaki",
        botanicalName = "Phyllanthus emblica",
        classicalRole = "Rasayana and antioxidant"
      )
    )

    val testMed = com.example.data.model.AyurvedaMedicine(
      id = "test_triphala_01",
      name = "Triphala Churna",
      ingredients = ingredients,
      dosageInstructions = "3 to 6 grams at bedtime with warm water or honey",
      benefits = listOf("Digestive cleansing", "Gentle detoxification", "Ocular health support"),
      category = com.example.data.model.FormulationCategory.CHURNA,
      stockUnits = 60
    )

    // Verify fields
    assertEquals("Triphala Churna", testMed.name)
    assertEquals(3, testMed.ingredients.size)
    assertEquals("3 to 6 grams at bedtime with warm water or honey", testMed.dosageInstructions)
    assertEquals(3, testMed.benefits.size)

    // Save via ViewModel
    medVm.saveMedicine(testMed)

    // Filter and search
    medVm.setSearchQuery("Triphala")
    assertEquals("Triphala", medVm.uiState.value.searchQuery)

    medVm.setDoshaFilter(DoshaType.TRIDOSHIC)
    assertEquals(DoshaType.TRIDOSHIC, medVm.uiState.value.selectedDosha)

    // Update stock
    medVm.updateStock(testMed.id, 85)

    // Select medicine for detail
    medVm.selectMedicine(testMed)
    assertEquals("test_triphala_01", medVm.uiState.value.selectedMedicine?.id)
  }

  @Test
  fun testMedicineCardBenefitsAndDetails() {
    val medicine = com.example.data.model.AyurvedaMedicine(
      id = "card_test_01",
      name = "Ashwagandha Lehyam",
      sanskritName = "Withania Somnifera Rasayana",
      ingredients = listOf(
        com.example.data.model.AyurvedaIngredient("Ashwagandha", "Withania somnifera", "Root", "500mg"),
        com.example.data.model.AyurvedaIngredient("Ghrita", "Clarified butter", "Vehicle", "100mg")
      ),
      dosageInstructions = "1-2 teaspoons twice daily with warm milk",
      benefits = listOf("Restores nervous vitality", "Relieves chronic stress", "Enhances stamina and Ojas"),
      primaryBenefit = "Restores nervous vitality and relieves fatigue",
      category = com.example.data.model.FormulationCategory.RASAYANA,
      stockUnits = 42
    )

    // Verify properties displayed on the card
    assertEquals("Ashwagandha Lehyam", medicine.name)
    assertEquals(3, medicine.effectiveBenefits.size)
    assertTrue(medicine.effectiveBenefits.contains("Restores nervous vitality"))
    assertTrue(medicine.effectiveBenefits.contains("Enhances stamina and Ojas"))
    assertEquals("1-2 teaspoons twice daily with warm milk", medicine.effectiveDosageInstructions)
    assertEquals(2, medicine.ingredients.size)
    assertEquals(42, medicine.stockUnits)
  }

  @Test
  fun testSearchMedicineByNameAndIngredientScope() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
    val medVm = com.example.ui.AyurvedaMedicineViewModel(context)

    val med1 = com.example.data.model.AyurvedaMedicine(
      id = "search_test_01",
      name = "Ashwagandha Rasayana",
      sanskritName = "Ashwagandha Avaleha",
      ingredients = listOf(
        com.example.data.model.AyurvedaIngredient("Ashwagandha", "Withania somnifera", "Root"),
        com.example.data.model.AyurvedaIngredient("Pippali", "Piper longum", "Fruit")
      ),
      benefits = listOf("Restores nervous vitality", "Relieves stress"),
      category = com.example.data.model.FormulationCategory.RASAYANA
    )

    val med2 = com.example.data.model.AyurvedaMedicine(
      id = "search_test_02",
      name = "Brahmi Vati",
      sanskritName = "Bacopa tablet",
      ingredients = listOf(
        com.example.data.model.AyurvedaIngredient("Brahmi", "Bacopa monnieri", "Whole herb"),
        com.example.data.model.AyurvedaIngredient("Shankhpushpi", "Convolvulus pluricaulis", "Whole plant")
      ),
      benefits = listOf("Enhances memory and cognition", "Calms Pitta"),
      category = com.example.data.model.FormulationCategory.VATI
    )

    val med3 = com.example.data.model.AyurvedaMedicine(
      id = "search_test_03",
      name = "Triphala Guggulu",
      sanskritName = "Classical detox pill",
      ingredients = listOf(
        com.example.data.model.AyurvedaIngredient("Haritaki", "Terminalia chebula", "Fruit rind"),
        com.example.data.model.AyurvedaIngredient("Bibhitaki", "Terminalia bellirica", "Fruit rind"),
        com.example.data.model.AyurvedaIngredient("Amalaki", "Phyllanthus emblica", "Fruit rind"),
        com.example.data.model.AyurvedaIngredient("Guggulu", "Commiphora mukul", "Resin"),
        com.example.data.model.AyurvedaIngredient("Pippali", "Piper longum", "Fruit")
      ),
      benefits = listOf("Joint mobility support", "Deep tissue detox"),
      category = com.example.data.model.FormulationCategory.VATI
    )

    val sampleMedicines = listOf(med1, med2, med3)

    // 1. Pure filtering test: Search Scope By Name
    val nameResults = com.example.ui.AyurvedaMedicineViewModel.filterMedicines(
      medicines = sampleMedicines,
      query = "Ashwagandha",
      searchScope = com.example.ui.MedicineSearchScope.NAME
    )
    assertEquals(1, nameResults.size)
    assertEquals("search_test_01", nameResults.first().id)

    // 2. Pure filtering test: Search Scope By Ingredient (Pippali is present in med1 and med3, but NOT in med2)
    val ingredientResults = com.example.ui.AyurvedaMedicineViewModel.filterMedicines(
      medicines = sampleMedicines,
      query = "Pippali",
      searchScope = com.example.ui.MedicineSearchScope.INGREDIENT
    )
    assertEquals(2, ingredientResults.size)
    assertTrue(ingredientResults.any { it.id == "search_test_01" })
    assertTrue(ingredientResults.any { it.id == "search_test_03" })
    org.junit.Assert.assertFalse(ingredientResults.any { it.id == "search_test_02" })

    // 3. Pure filtering test: Search Scope ALL (Matches either name, sanskrit, or ingredient)
    val allResults = com.example.ui.AyurvedaMedicineViewModel.filterMedicines(
      medicines = sampleMedicines,
      query = "Brahmi",
      searchScope = com.example.ui.MedicineSearchScope.ALL
    )
    assertEquals(1, allResults.size)
    assertEquals("search_test_02", allResults.first().id)

    // 4. ViewModel State and helper functions test
    medVm.filterByName("Ashwagandha")
    assertEquals(com.example.ui.MedicineSearchScope.NAME, medVm.uiState.value.searchScope)
    assertEquals("Ashwagandha", medVm.uiState.value.searchQuery)

    medVm.filterByIngredient("Haritaki")
    assertEquals(com.example.ui.MedicineSearchScope.INGREDIENT, medVm.uiState.value.searchScope)
    assertEquals("Haritaki", medVm.uiState.value.searchQuery)

    medVm.setSearchScope(com.example.ui.MedicineSearchScope.ALL)
    medVm.setSearchQuery("Detox")
    assertEquals(com.example.ui.MedicineSearchScope.ALL, medVm.uiState.value.searchScope)
    assertEquals("Detox", medVm.uiState.value.searchQuery)

    // Test clearSearch
    medVm.clearSearch()
    assertEquals("", medVm.uiState.value.searchQuery)
    assertEquals(com.example.ui.MedicineSearchScope.ALL, medVm.uiState.value.searchScope)
  }
}
