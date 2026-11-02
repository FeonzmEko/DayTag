# Device Fact Check

Checked on 2026-11-02 against Xiaomi's official product specifications.

## Xiaomi 15 Ultra

- Official product page: <https://www.mi.com/global/product/xiaomi-15-ultra/specs/>
- Display: 6.73-inch WQHD+ AMOLED
- Resolution: 3200 × 1440
- Pixel density: 522 ppi
- System shown by Xiaomi: Xiaomi HyperOS 2

## Planning consequence

The app should not hard-code a single pixel layout. It will use responsive Compose layouts and an emulator close to the device aspect ratio for visual QA. The local machine has an Android 15 / API 35 Google APIs image, but no HyperOS system image, so Xiaomi launcher-specific behavior remains a final-device verification item rather than an emulator claim.

