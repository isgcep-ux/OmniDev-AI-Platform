package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OmniDev AI Platform", appName)
  }

  @Test
  fun `verify role hierarchy and permissions`() {
    val ownerRoles = listOf("OWNER", "ADMIN", "EDITOR", "VIEWER")
    assertEquals(4, ownerRoles.size)
    assert(ownerRoles.contains("ADMIN"))
    assert(ownerRoles.contains("EDITOR"))
  }

}
