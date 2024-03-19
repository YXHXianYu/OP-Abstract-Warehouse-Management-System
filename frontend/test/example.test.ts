import { assert, expect, test } from 'vitest'
import { shallowMount } from '@vue/test-utils'
import LoginVue from '@/views/Login.vue'

// You could refer to this document: https://vitest.dev/guide/
// Vitest will run all files that named with .test.ts or .test.js
// => So, you can place .test.ts in ./src, ./test, or any folder you want 

// Test Example 1: Test some std lib functions
test('Math.sqrt()', () => {
    // expect
    expect(Math.sqrt(4)).toBe(2)
    // assert
    assert.equal(Math.sqrt(9), 3)
})

// Test Example 2: Click some button and check the result
test('Login.vue', async () => {
    const wrapper = shallowMount(LoginVue)
    await wrapper.find('el-button').trigger('click')

    // etc.... (TODO)
})