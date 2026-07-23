/**
 * 前端 SM2 加密工具
 *
 * <h3>使用方式</h3>
 * <pre>
 * import { encryptPassword } from '@/utils/sm2';
 *
 * // 登录时先加密密码再发送
 * const encrypted = encryptPassword(plainPassword, publicKeyHex);
 * axios.post('/auth/login/password', {
 *   username: 'admin',
 *   password: encrypted   // SM2 加密后的 Base64 密文
 * });
 * </pre>
 *
 * <h3>密钥管理</h3>
 * 公钥不应硬编码在前端代码中，建议从后端接口动态获取（如 GET /auth/public-key）。
 * 此处提供工具函数，不强制绑定公钥来源。
 */

import { sm2 } from 'sm-crypto';

/**
 * 使用 SM2 公钥加密密码
 *
 * @param plainPassword - 明文密码
 * @param publicKeyHex  - 十六进制公钥（X.509 编码，04 开头）
 * @returns Base64 密文（C1C3C2 模式），可直接发送给后端
 */
export function encryptPassword(plainPassword: string, publicKeyHex: string): string {
  if (!plainPassword || !publicKeyHex) {
    throw new Error('明文密码和公钥均不能为空');
  }

  // sm-crypto 的 doEncrypt 返回 hex 格式密文（C1C3C2），
  // 但 sm-crypto 输出的 C1 分量不包含 04 前缀（仅 x||y，64 字节），
  // 而 BouncyCastle 的 SM2Engine 要求 C1 是完整公钥编码（04||x||y，65 字节）
  // 因此需要在最前面补上 04 前缀
  const cipherHex = sm2.doEncrypt(plainPassword, publicKeyHex, 1); // mode=1 即 C1C3C2
  const fullCipherHex = '04' + cipherHex;

  // 转 Base64（与后端 SM2Utils.decryptFromBase64 对应）
  const cipherBytes = hexToBytes(fullCipherHex);
  return bytesToBase64(cipherBytes);
}

/**
 * Hex 字符串转 Uint8Array
 */
function hexToBytes(hex: string): Uint8Array {
  const bytes = new Uint8Array(hex.length / 2);
  for (let i = 0; i < hex.length; i += 2) {
    bytes[i / 2] = parseInt(hex.substring(i, i + 2), 16);
  }
  return bytes;
}

/**
 * Uint8Array 转 Base64 字符串
 */
function bytesToBase64(bytes: Uint8Array): string {
  let binary = '';
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary);
}
