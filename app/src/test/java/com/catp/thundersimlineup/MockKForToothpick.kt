package com.catp.thundersimlineup


/**
 * https://github.com/stephanenicolas/toothpick/issues/373
 * Mockk has no field annotation, and TP is just ignores all the mocked fields,
 * quick workaround for this, is to make annotation with that filed,
 * which will be additionally added to all mocked fields.
 * For example:
    @MockKForToothpick
    @MockK(relaxed = true)
    lateinit var dao: LineupDao
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class MockKForToothpick