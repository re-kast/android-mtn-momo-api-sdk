import {themes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const lightCodeTheme = themes.github;
const darkCodeTheme = themes.dracula;

const config: Config = {
    title: 'MTN MOMO Android SDK Documentation',
    tagline: 'MTN MOMO Android SDK Documentation',
    favicon: 'img/favicon.ico',
    url: 'https://mtn-momo-sdk.rekast.io/',
    baseUrl: '/',
    organizationName: 're-kast',
    projectName: 'android-mtn-momo-api-sdk',
    onBrokenLinks: 'ignore',
    onBrokenMarkdownLinks: 'warn',
    staticDirectories: ['static'],

    // Even if you don't use internationalization, you can use this field to set
    // useful metadata like html lang. For example, if your site is Chinese, you
    // may want to replace "en" with "zh-Hans".
    i18n: {
        defaultLocale: 'en',
        locales: ['en']
    },
    presets: [
        [
            'classic',
            {
                docs: {
                    sidebarPath: require.resolve('./sidebars.ts'),
                    routeBasePath: '/',
                    editUrl:
                        'https://github.com/re-kast/android-mtn-momo-api-sdk/tree/develop',
                },
                blog: false,
                theme: {
                    customCss: [require.resolve('./static/css/custom.css')],
                },
            } satisfies Preset.Options,
        ],
    ],
    themeConfig: {
        colorMode: {
            defaultMode: 'dark',
            disableSwitch: true,
            respectPrefersColorScheme: false,
        },
        navbar: {
            title: '| MTN MOMO ANDROID SDK',
            logo: {
                alt: '| MTN MOMO ANDROID SDK',
                src: 'img/logo-icon.svg',
            },
            items: [
                {
                    href: 'https://mtn-momo-sdk.rekast.io/dokka/',
                    label: 'KDocs',
                    position: 'right',
                },
                {
                    type: 'docsVersionDropdown',
                    label: 'KDocs',
                    position: 'right',
                },
            ],
        },
        footer: {
            style: 'dark',
            copyright: `Copyright © ${new Date().getFullYear()} Re.Kast Limited.`,
        },
        prism: {
            theme: lightCodeTheme,
            darkTheme: darkCodeTheme,
            additionalLanguages: ['bash', 'diff', 'json'],
        },
        algolia: {
            appId: '80CQ8JPRJC',
            apiKey: 'aec8155b4175df7f2439fa94cf428f62',
            indexName: 'momoandroidsdk',
            searchPagePath: 'search'
        },
    } satisfies Preset.ThemeConfig,
    
};

export default config;
