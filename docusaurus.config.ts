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
    trailingSlash: false,
    staticDirectories: ['static'],
    markdown: {
        hooks: {
            onBrokenMarkdownLinks: 'warn',
        },
    },
    i18n: {
        defaultLocale: 'en',
        locales: ['en'],
    },
    presets: [
        [
            'classic',
            {
                docs: {
                    sidebarPath: require.resolve('./sidebars.ts'),
                    routeBasePath: '/',
                    editUrl: 'https://github.com/re-kast/android-mtn-momo-api-sdk/tree/develop',
                    showLastUpdateTime: true,
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
        docs: {
            sidebar: {
                hideable: true,
                autoCollapseCategories: true,
            },
        },
        tableOfContents: {
            minHeadingLevel: 2,
            maxHeadingLevel: 3,
        },
        navbar: {
            title: '| MTN MOMO ANDROID SDK',
            hideOnScroll: true,
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
                    position: 'right',
                },
                {
                    href: 'https://github.com/re-kast/android-mtn-momo-api-sdk',
                    'aria-label': 'GitHub',
                    className: 'navbar__icon navbar__github',
                    position: 'right',
                    html: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><path fill="currentColor" d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/></svg>',
                },
            ],
        },
        footer: {
            style: 'dark',
            links: [
                {
                    title: 'Docs',
                    items: [
                        {label: 'Overview', to: '/'},
                        {label: 'Library Setup', to: '/Documentation/usage'},
                        {label: 'Authentication', to: '/Documentation/api-reference/authentication'},
                        {label: 'Collection', to: '/Documentation/api-reference/collection'},
                        {label: 'KDocs', href: 'https://mtn-momo-sdk.rekast.io/dokka/'},
                    ],
                },
                {
                    title: 'Engineering',
                    items: [
                        {label: 'Developer Setup', to: '/engineering/getting-started/developer-setup'},
                        {label: 'Testing', to: '/engineering/getting-started/testing'},
                        {label: 'Code Contributions', to: '/engineering/contributions/code-contribution'},
                    ],
                },
                {
                    title: 'More',
                    items: [
                        {label: 'GitHub', href: 'https://github.com/re-kast/android-mtn-momo-api-sdk'},
                        {label: 'Issues', href: 'https://github.com/re-kast/android-mtn-momo-api-sdk/issues'},
                        {label: 'MTN MoMo Developer Portal', href: 'https://momodeveloper.mtn.com/'},
                        {label: 'Security Policy', href: 'https://github.com/re-kast/android-mtn-momo-api-sdk/blob/develop/SECURITY.md'},
                        {label: 'License', href: 'https://github.com/re-kast/android-mtn-momo-api-sdk/blob/develop/LICENSE'},
                    ],
                },
            ],
            copyright: `Copyright © ${new Date().getFullYear()} Re.Kast Limited.`,
        },
        prism: {
            theme: lightCodeTheme,
            darkTheme: darkCodeTheme,
            additionalLanguages: ['bash', 'diff', 'json', 'kotlin', 'yaml', 'properties'],
        },
        algolia: {
            appId: 'MM0YM0A204',
            apiKey: 'b598da25f0147ae4263e713fffbee542',
            indexName: 'MTN MOMO Crawler',
            contextualSearch: true,
            searchPagePath: 'search',
            searchParameters: {},
            replaceSearchResultPathname: {
                from: '/docs/',
                to: '/',
            },
        },
    } satisfies Preset.ThemeConfig,
};

export default config;
