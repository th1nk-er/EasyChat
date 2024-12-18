package top.th1nk.easychat.utils;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtils {
    /**
     * 获取当前线程的 HttpServletRequest
     *
     * @return HttpServletRequest null则表示当前线程没有绑定 HttpServletRequest
     */
    @Nullable
    public static HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private static String getClientIp(@NotNull HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 获取客户端IP
     *
     * @return 客户端IP 获取失败返回空字符串
     */
    public static String getClientIp() {
        HttpServletRequest currentRequest = getCurrentRequest();
        if (currentRequest == null) return "";
        return getClientIp(currentRequest);
    }

    /**
     * 获取客户端User-Agent
     *
     * @return 客户端User-Agent
     */
    @NotNull
    public static String getUserAgent() {
        HttpServletRequest currentRequest = getCurrentRequest();
        if (currentRequest == null) return "";
        String ua = currentRequest.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    /**
     * 获取客户端token
     *
     * @return token字符串
     */
    @NotNull
    public static String getUserTokenString() {
        HttpServletRequest currentRequest = getCurrentRequest();
        if (currentRequest == null) return "";
        String token = currentRequest.getHeader("Authorization");
        return token != null ? token : "";
    }
}
