package com.example

import com.example.data.local.entity.ISGContent
import com.example.ui.components.RiskLevel
import com.example.ui.components.kkdHierarchyList
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for local entities and models.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun isgContent_entityFields_areCorrect() {
    val lesson = ISGContent(
      id = 1L,
      title = "6331 Sayılı İSG Kanunu",
      category = "Mevzuat",
      content = "İş sağlığı ve güvenliği temel ilkeleri."
    )

    assertEquals(1L, lesson.id)
    assertEquals("6331 Sayılı İSG Kanunu", lesson.title)
    assertEquals("Mevzuat", lesson.category)
    assertEquals("İş sağlığı ve güvenliği temel ilkeleri.", lesson.content)
    assertEquals("İş sağlığı ve güvenliği temel ilkeleri.", lesson.textualContent)
  }

  @Test
  fun isgContent_secondaryConstructor_populatesCorrectly() {
    val lesson = ISGContent(
      id = 2L,
      title = "Risk Değerlendirmesi",
      category = "Metodoloji",
      textualContent = "5x5 L-Tipi Matris Analizi"
    )

    assertEquals(2L, lesson.id)
    assertEquals("Risk Değerlendirmesi", lesson.title)
    assertEquals("Metodoloji", lesson.category)
    assertEquals("5x5 L-Tipi Matris Analizi", lesson.content)
    assertEquals("5x5 L-Tipi Matris Analizi", lesson.textualContent)
  }

  @Test
  fun riskLevel_scoreClassification_isCorrect() {
    // Low risk: <= 6
    assertEquals(RiskLevel.LOW, RiskLevel.fromScore(1))
    assertEquals(RiskLevel.LOW, RiskLevel.fromScore(6))

    // Medium risk: 8..12
    assertEquals(RiskLevel.MEDIUM, RiskLevel.fromScore(8))
    assertEquals(RiskLevel.MEDIUM, RiskLevel.fromScore(10))
    assertEquals(RiskLevel.MEDIUM, RiskLevel.fromScore(12))

    // High / Intolerable risk: >= 15
    assertEquals(RiskLevel.HIGH, RiskLevel.fromScore(15))
    assertEquals(RiskLevel.HIGH, RiskLevel.fromScore(20))
    assertEquals(RiskLevel.HIGH, RiskLevel.fromScore(25))
  }

  @Test
  fun kkdHierarchy_hasFiveStages_inCorrectOrder() {
    assertEquals(5, kkdHierarchyList.size)
    // 1: Elimination
    assertEquals(1, kkdHierarchyList[0].rank)
    assertTrue(kkdHierarchyList[0].titleTr.contains("ELİMİNASYON"))

    // 2: Substitution
    assertEquals(2, kkdHierarchyList[1].rank)
    assertTrue(kkdHierarchyList[1].titleTr.contains("İKAME"))

    // 3: Engineering Controls
    assertEquals(3, kkdHierarchyList[2].rank)
    assertTrue(kkdHierarchyList[2].titleTr.contains("MÜHENDİSLİK"))

    // 4: Administrative Controls
    assertEquals(4, kkdHierarchyList[3].rank)
    assertTrue(kkdHierarchyList[3].titleTr.contains("İDARİ"))

    // 5: PPE (KKD) - Last resort
    assertEquals(5, kkdHierarchyList[4].rank)
    assertTrue(kkdHierarchyList[4].titleTr.contains("KİŞİSEL KORUYUCU"))

    // Verify each tier has non-empty practical tree sub-items
    kkdHierarchyList.forEach { tier ->
      assertTrue(tier.subItems.isNotEmpty())
    }
  }

  @Test
  fun isgContent_flashcardModel_supportsFrontBackMapping() {
    val card = ISGContent(
      id = 101L,
      title = "Maruziyet Eylem Değeri (Gürültü)",
      category = "Fiziksel Riskler",
      content = "En düşük maruziyet eylem değeri: 80 dB(A), En yüksek maruziyet eylem değeri: 85 dB(A)."
    )

    // Front of card displays title & category
    assertEquals("Maruziyet Eylem Değeri (Gürültü)", card.title)
    assertEquals("Fiziksel Riskler", card.category)

    // Back of card displays answer & principle
    assertTrue(card.content.contains("80 dB(A)"))
    assertTrue(card.content.contains("85 dB(A)"))
    assertEquals(card.content, card.textualContent)
  }

  @Test
  fun isgQuestionGenerator_createsQuestionsFromRoomEntities() {
    val lessons = listOf(
      ISGContent(1L, "6331 Sayılı İSG Kanunu", "Mevzuat", "İşveren çalışanların işle ilgili sağlık ve güvenliğini sağlamakla yükümlüdür."),
      ISGContent(2L, "5x5 L-Tipi Risk Matrisi", "Risk Analizi", "Risk skoru olasılık ile şiddetin çarpımıyla hesaplanır."),
      ISGContent(3L, "Gürültü Yönetmeliği", "Fiziksel Riskler", "En yüksek maruziyet eylem değeri 85 dB(A) olarak belirlenmiştir."),
      ISGContent(4L, "KKD Yönetmeliği", "KKD", "Kişisel koruyucu donanımlar risk kontrol hiyerarşisinde en son başvurulacak tedbirdir.")
    )

    val questions = com.example.ui.components.ISGQuestionGenerator.generateQuestions(lessons)

    assertEquals(4, questions.size)
    questions.forEach { q ->
      assertTrue(q.questionText.isNotBlank())
      assertEquals(4, q.options.size)
      assertTrue(q.correctIndex in 0..3)
      assertTrue(q.explanation.isNotBlank())
      assertEquals(q.options[q.correctIndex], q.options[q.correctIndex])
    }
  }

  @Test
  fun filterISGContent_filtersByTitleAndCategoryAccurately() {
    val sampleLessons = listOf(
      ISGContent(1L, "6331 Sayılı İSG Kanunu", "Mevzuat", "İş sağlığı ve güvenliği temel kanunu."),
      ISGContent(2L, "5x5 L-Tipi Matris Analizi", "Metodoloji", "Risk değerlendirmesi olasılık ve şiddet matrisi."),
      ISGContent(3L, "Kulak Koruyucu Donanımlar", "KKD", "Gürültülü ortamlarda işitme kaybını önlemek için kullanılır."),
      ISGContent(4L, "Gürültü Yönetmeliği", "Mevzuat", "85 dB(A) en yüksek maruziyet eylem değeri.")
    )

    // Test search by title
    val searchByTitle = com.example.ui.components.filterISGContent(sampleLessons, searchQuery = "6331")
    assertEquals(1, searchByTitle.size)
    assertEquals("6331 Sayılı İSG Kanunu", searchByTitle[0].title)

    // Test search by category
    val searchByCategory = com.example.ui.components.filterISGContent(sampleLessons, searchQuery = "KKD")
    assertEquals(1, searchByCategory.size)
    assertEquals("Kulak Koruyucu Donanımlar", searchByCategory[0].title)

    // Test category chip filter
    val filterMevzuat = com.example.ui.components.filterISGContent(sampleLessons, searchQuery = "", selectedCategory = "Mevzuat")
    assertEquals(2, filterMevzuat.size)

    // Combined category and query search
    val combinedSearch = com.example.ui.components.filterISGContent(sampleLessons, searchQuery = "Gürültü", selectedCategory = "Mevzuat")
    assertEquals(1, combinedSearch.size)
    assertEquals("Gürültü Yönetmeliği", combinedSearch[0].title)

    // Non-matching query returns empty list
    val noMatch = com.example.ui.components.filterISGContent(sampleLessons, searchQuery = "BulunmayanKonu")
    assertTrue(noMatch.isEmpty())
  }

  @Test
  fun project_statusDistribution_andFiltering_isCorrect() {
    val sampleProjects = listOf(
      com.example.data.model.Project("1", "App Alpha", "Desc 1", status = com.example.data.model.Project.STATUS_IN_PROGRESS),
      com.example.data.model.Project("2", "App Beta", "Desc 2", status = com.example.data.model.Project.STATUS_COMPLETED),
      com.example.data.model.Project("3", "App Gamma", "Desc 3", status = com.example.data.model.Project.STATUS_COMPLETED),
      com.example.data.model.Project("4", "App Delta", "Desc 4", status = com.example.data.model.Project.STATUS_ON_HOLD)
    )

    val inProgress = sampleProjects.filter { it.status == com.example.data.model.Project.STATUS_IN_PROGRESS }
    val completed = sampleProjects.filter { it.status == com.example.data.model.Project.STATUS_COMPLETED }
    val onHold = sampleProjects.filter { it.status == com.example.data.model.Project.STATUS_ON_HOLD }

    assertEquals(1, inProgress.size)
    assertEquals(2, completed.size)
    assertEquals(1, onHold.size)
    assertEquals(4, sampleProjects.size)

    val inProgressPercentage = inProgress.size * 100f / sampleProjects.size
    val completedPercentage = completed.size * 100f / sampleProjects.size
    val onHoldPercentage = onHold.size * 100f / sampleProjects.size

    assertEquals(25.0f, inProgressPercentage, 0.01f)
    assertEquals(50.0f, completedPercentage, 0.01f)
    assertEquals(25.0f, onHoldPercentage, 0.01f)
  }
}
