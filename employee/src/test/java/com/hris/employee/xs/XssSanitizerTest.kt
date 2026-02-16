//package com.hris.employee.xs
//
//import com.hris.employee.utils.XssSanitizer
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.owasp.encoder.Encode
//
////need config in ide or driver kotln
//internal class XssSanitizerTest {
//    private var xssSanitizer: XssSanitizer? = null
//
//    @BeforeEach
//    fun setUp() {
//        xssSanitizer = XssSanitizer()
//    }
//
//    // ===============================
//    // NULL TESTS
//    // ===============================
//    @Test
//    @DisplayName("Should return null when input is null")
//    fun shouldReturnNullWhenInputIsNull() {
//        Assertions.assertNull(xssSanitizer!!.sanitizeForHtml(null))
//        Assertions.assertNull(xssSanitizer!!.sanitizeForJavaScript(null))
//        Assertions.assertNull(xssSanitizer!!.sanitizeForCss(null))
//        Assertions.assertNull(xssSanitizer!!.sanitizeForHtmlAttribute(null))
//    }
//
//    // ===============================
//    // HTML TESTS
//    // ===============================
//    @Test
//    @DisplayName("Should sanitize dangerous HTML input")
//    fun shouldSanitizeHtml() {
//        val input = "<script>alert('xss')</script>"
//
//        val result = xssSanitizer!!.sanitizeForHtml(input)
//        val expected = Encode.forHtml(input)
//
//        Assertions.assertEquals(expected, result)
//        Assertions.assertNotEquals(input, result)
//    }
//
//    @Test
//    @DisplayName("Should not modify safe HTML input")
//    fun shouldNotModifySafeHtml() {
//        val input = "HelloWorld123"
//
//        val result = xssSanitizer!!.sanitizeForHtml(input)
//
//        Assertions.assertEquals(input, result)
//    }
//
//    // ===============================
//    // JAVASCRIPT TESTS
//    // ===============================
//    @Test
//    @DisplayName("Should sanitize JavaScript input")
//    fun shouldSanitizeJavaScript() {
//        val input = "alert('xss')"
//
//        val result = xssSanitizer!!.sanitizeForJavaScript(input)
//        val expected = Encode.forJavaScript(input)
//
//        Assertions.assertEquals(expected, result)
//        Assertions.assertNotEquals(input, result)
//    }
//
//    // ===============================
//    // CSS TESTS
//    // ===============================
//    @Test
//    @DisplayName("Should sanitize CSS input")
//    fun shouldSanitizeCss() {
//        val input = "background:url(javascript:alert(1))"
//
//        val result = xssSanitizer!!.sanitizeForCss(input)
//        val expected = Encode.forCssString(input)
//
//        Assertions.assertEquals(expected, result)
//        Assertions.assertNotEquals(input, result)
//    }
//
//    // ===============================
//    // HTML ATTRIBUTE TESTS
//    // ===============================
//    @Test
//    @DisplayName("Should sanitize HTML attribute input")
//    fun shouldSanitizeHtmlAttribute() {
//        val input = "\" onmouseover=\"alert(1)"
//
//        val result = xssSanitizer!!.sanitizeForHtmlAttribute(input)
//        val expected = Encode.forHtmlAttribute(input)
//
//        Assertions.assertEquals(expected, result)
//        Assertions.assertNotEquals(input, result)
//    }
//}
