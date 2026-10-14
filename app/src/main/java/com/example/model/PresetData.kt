package com.example.model

object PresetData {
    val sampleScenes = listOf(
        PresetScene(
            id = "preset_living_room",
            title = "Living Room & Furniture",
            category = "Indoor Scene",
            description = "Cozy indoor lounge with couch, coffee table, and coffee cup.",
            result = AnalysisResult(
                id = "res_living_room",
                sceneDescription = SceneDescription(
                    caption = "A bright indoor living room featuring a fabric sofa, a low wooden coffee table with a steaming ceramic mug, and a houseplant by the window.",
                    indoorOutdoor = "Indoor (Living Room)",
                    lighting = "Natural daylight, well-lit",
                    safetyAdvice = "Low coffee table directly ahead at 1.1m. Clear walking path on your left.",
                    ocrText = "Cozy Home Decor - Issue 12"
                ),
                detectedObjects = listOf(
                    DetectedObject(
                        id = "obj_1",
                        label = "Coffee Table",
                        confidence = 0.94f,
                        boundingBox = NormalizedRect(0.25f, 0.55f, 0.75f, 0.85f),
                        distanceMeters = 1.1f,
                        position = SpatialPosition.CENTER,
                        depth = DepthCategory.FOREGROUND,
                        isHazard = true,
                        description = "Low obstacle directly in front"
                    ),
                    DetectedObject(
                        id = "obj_2",
                        label = "Sofa",
                        confidence = 0.98f,
                        boundingBox = NormalizedRect(0.10f, 0.30f, 0.90f, 0.65f),
                        distanceMeters = 2.4f,
                        position = SpatialPosition.CENTER,
                        depth = DepthCategory.MIDGROUND,
                        isHazard = false,
                        description = "3-seater fabric couch"
                    ),
                    DetectedObject(
                        id = "obj_3",
                        label = "Coffee Cup",
                        confidence = 0.91f,
                        boundingBox = NormalizedRect(0.48f, 0.58f, 0.58f, 0.68f),
                        distanceMeters = 1.0f,
                        position = SpatialPosition.CENTER,
                        depth = DepthCategory.FOREGROUND,
                        isHazard = false,
                        description = "Ceramic mug on the table"
                    ),
                    DetectedObject(
                        id = "obj_4",
                        label = "Potted Plant",
                        confidence = 0.88f,
                        boundingBox = NormalizedRect(0.72f, 0.25f, 0.95f, 0.60f),
                        distanceMeters = 3.2f,
                        position = SpatialPosition.RIGHT,
                        depth = DepthCategory.BACKGROUND,
                        isHazard = false,
                        description = "Monstera plant in ceramic pot"
                    )
                ),
                audioNarrationText = "Scene Overview: Bright indoor living room with a fabric sofa and low coffee table. Caution: Low coffee table 1.1 meters directly ahead in the center. Clear path available to your left.",
                isSample = true
            )
        ),
        PresetScene(
            id = "preset_crosswalk",
            title = "Pedestrian Crosswalk & Street",
            category = "Outdoor Navigation",
            description = "City street corner with crosswalk, traffic signal, and pedestrians.",
            result = AnalysisResult(
                id = "res_crosswalk",
                sceneDescription = SceneDescription(
                    caption = "An urban pedestrian crosswalk with painted white stripes, a pedestrian traffic light, cars stopped at the intersection, and pedestrians walking.",
                    indoorOutdoor = "Outdoor (Urban Street)",
                    lighting = "Bright direct sunlight",
                    safetyAdvice = "Walk sign is active. Pedestrian crossing ahead in 2.0 meters. Car stationary on the right at 4.5 meters.",
                    ocrText = "WALK - 5TH AVE & MAIN ST"
                ),
                detectedObjects = listOf(
                    DetectedObject(
                        id = "obj_street_1",
                        label = "Pedestrian Signal",
                        confidence = 0.96f,
                        boundingBox = NormalizedRect(0.70f, 0.12f, 0.88f, 0.45f),
                        distanceMeters = 2.2f,
                        position = SpatialPosition.RIGHT,
                        depth = DepthCategory.MIDGROUND,
                        isHazard = false,
                        description = "Walk sign illuminated white"
                    ),
                    DetectedObject(
                        id = "obj_street_2",
                        label = "Pedestrian",
                        confidence = 0.92f,
                        boundingBox = NormalizedRect(0.20f, 0.35f, 0.38f, 0.78f),
                        distanceMeters = 1.8f,
                        position = SpatialPosition.LEFT,
                        depth = DepthCategory.MIDGROUND,
                        isHazard = false,
                        description = "Person walking on left"
                    ),
                    DetectedObject(
                        id = "obj_street_3",
                        label = "Car",
                        confidence = 0.95f,
                        boundingBox = NormalizedRect(0.60f, 0.42f, 0.98f, 0.80f),
                        distanceMeters = 4.5f,
                        position = SpatialPosition.RIGHT,
                        depth = DepthCategory.BACKGROUND,
                        isHazard = false,
                        description = "Silver sedan stopped at crosswalk"
                    )
                ),
                audioNarrationText = "Outdoor street crossing. Walk signal is white and active. Pedestrian 1.8 meters to your left. Safe to cross along marked pavement.",
                isSample = true
            )
        ),
        PresetScene(
            id = "preset_staircase",
            title = "Indoor Staircase & Exit",
            category = "Hazard & Navigation",
            description = "Indoor hallway with descending stairs and illuminated Exit sign.",
            result = AnalysisResult(
                id = "res_staircase",
                sceneDescription = SceneDescription(
                    caption = "A carpeted office hallway leading to a descending flight of stairs with stainless steel handrails and a green illuminated Exit sign above the door.",
                    indoorOutdoor = "Indoor (Hallway)",
                    lighting = "Artificial ceiling fluorescent light",
                    safetyAdvice = "HAZARD: Descending stairs starting 1.2 meters directly ahead! Use handrail on your right.",
                    ocrText = "EXIT - STAIRWELL B"
                ),
                detectedObjects = listOf(
                    DetectedObject(
                        id = "obj_stair_1",
                        label = "Stairs (Descending)",
                        confidence = 0.97f,
                        boundingBox = NormalizedRect(0.15f, 0.55f, 0.85f, 0.95f),
                        distanceMeters = 1.2f,
                        position = SpatialPosition.CENTER,
                        depth = DepthCategory.FOREGROUND,
                        isHazard = true,
                        description = "Descending staircase - step hazard"
                    ),
                    DetectedObject(
                        id = "obj_stair_2",
                        label = "Handrail",
                        confidence = 0.93f,
                        boundingBox = NormalizedRect(0.78f, 0.40f, 0.92f, 0.85f),
                        distanceMeters = 1.1f,
                        position = SpatialPosition.RIGHT,
                        depth = DepthCategory.FOREGROUND,
                        isHazard = false,
                        description = "Metal support handrail"
                    ),
                    DetectedObject(
                        id = "obj_stair_3",
                        label = "Exit Sign",
                        confidence = 0.99f,
                        boundingBox = NormalizedRect(0.40f, 0.08f, 0.60f, 0.22f),
                        distanceMeters = 3.8f,
                        position = SpatialPosition.CENTER,
                        depth = DepthCategory.BACKGROUND,
                        isHazard = false,
                        description = "Green illuminated EXIT sign"
                    )
                ),
                audioNarrationText = "Warning! Descending stairs 1.2 meters directly ahead. Please hold the handrail located on your right side.",
                isSample = true
            )
        )
    )
}
