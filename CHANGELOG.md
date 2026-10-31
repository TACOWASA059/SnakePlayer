# Changelog

## 26.3-v1.5.0

### Added
- **Minecraft 26.3 Port**:
  - Ported to Minecraft 26.3 using MultiLoader-Template 26.3.
- **Connected Body Mesh Rendering**:
  - Unified snake body rendering to a seamless, continuous connected tube mesh (`ConnectedBodyRenderer`).
- **Body Part Hitbox Rendering (F3 + B)**:
  - Added F3 + B hitbox rendering for all snake body segments (`PlayerPart`) in both third-person and first-person perspectives on Fabric and NeoForge.

### Fixed
- **Head Sphere Lighting**:
  - Fixed lighting/shadow on player head (sphere) being inverted by correcting polygon vertex winding order to CCW (front-facing).
- **Body Segment Lighting**:
  - Corrected surface normals and polygon winding orders to ensure consistent directional lighting across all body parts.

## 26.2-v1.5.0

### Added
- **Connected Body Mesh Rendering**:
  - Unified snake body rendering to a seamless, continuous connected tube mesh (`ConnectedBodyRenderer`).
- **Body Part Hitbox Rendering (F3 + B)**:
  - Added F3 + B hitbox rendering for all snake body segments (`PlayerPart`) in both third-person and first-person perspectives on Fabric and NeoForge.

### Fixed
- **Head Sphere Lighting**:
  - Fixed lighting/shadow on player head (sphere) being inverted by correcting polygon vertex winding order to CCW (front-facing).
- **Body Segment Lighting**:
  - Corrected surface normals and polygon winding orders to ensure consistent directional lighting across all body parts.
