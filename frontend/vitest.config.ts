import { mergeConfig } from 'vite';
import { defineConfig } from 'vitest/config';
import path from 'path';
import viteConfig from './vite.config'

export default mergeConfig(
    viteConfig,
    defineConfig({
        resolve: {
            alias: {
                '@': path.resolve(__dirname, './src'),
            },
        },
        test: {
            environment: 'jsdom',
        },
    }),
);