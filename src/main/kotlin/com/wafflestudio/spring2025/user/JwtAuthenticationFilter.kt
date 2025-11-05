package com.wafflestudio.spring2025.user

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (isPublicPath(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveToken(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val username = jwtTokenProvider.getUsername(token)
            request.setAttribute("username", username)
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    private fun isPublicPath(path: String): Boolean {
        val pathMatcher = AntPathMatcher()
        val publicPaths =
            arrayOf(
                "/api/v1/auth/**",
                "/api/v1/courses",
                "/api/v1/courses/**",
                // Swagger/OpenAPI Paths
                "/swagger-ui/**", // For the main UI assets
                "/v3/api-docs/**", // For the OpenAPI specification JSON/YAML
                "/v3/api-docs.yaml", // Sometimes a direct path is used
                "/v3/api-docs.json", // Sometimes a direct path is used
                "/webjars/**", // For older setups or specific static assets
            )

        return publicPaths.any { publicPath ->
            pathMatcher.match(publicPath, path)
        }
    }
}
