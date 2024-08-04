package com.dyk.test.security.jwt

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("isAuthenticated()")
annotation class JwtAuth