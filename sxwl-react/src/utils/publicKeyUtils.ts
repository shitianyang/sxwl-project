import { getPublicKey, type PublicKeyResult } from '@/api/authApi';

const CACHE_KEY = 'sxwl_sm2_public_key';
const EXPIRE_SKEW_MS = 60_000;

let memoryCache: PublicKeyResult | null = null;

function normalizeExpiresAt(expiresAt: number) {
  return expiresAt < 10_000_000_000 ? expiresAt * 1000 : expiresAt;
}

function isValid(cache: PublicKeyResult | null) {
  if (!cache?.publicKey || !cache.expiresAt) {
    return false;
  }
  return normalizeExpiresAt(cache.expiresAt) - EXPIRE_SKEW_MS > Date.now();
}

function readCache() {
  if (isValid(memoryCache)) {
    return memoryCache;
  }

  const raw = sessionStorage.getItem(CACHE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as PublicKeyResult;
    if (isValid(parsed)) {
      memoryCache = parsed;
      return parsed;
    }
  } catch {
    // ignore broken cache
  }

  sessionStorage.removeItem(CACHE_KEY);
  memoryCache = null;
  return null;
}

function writeCache(publicKey: PublicKeyResult) {
  memoryCache = publicKey;
  sessionStorage.setItem(CACHE_KEY, JSON.stringify(publicKey));
}

export async function getCachedPublicKey() {
  const cached = readCache();
  if (cached) {
    return cached.publicKey;
  }

  const res = await getPublicKey();
  const publicKey = res.data.data;
  writeCache(publicKey);
  return publicKey.publicKey;
}

export function invalidatePublicKeyCache() {
  memoryCache = null;
  sessionStorage.removeItem(CACHE_KEY);
}
