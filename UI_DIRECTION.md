# UI direction — Campus Connect

Reference: [heydamir/unslop](https://github.com/heydamir/unslop), used as a design-quality guide. Do not copy its examples literally; apply its principles to Campus Connect.

## Direction

**Product:** A campus resource finder for students who need to quickly discover which labs, systems, software, and specialist equipment exist.

**Character:** practical, exact, quietly academic

**World:** campus directories, lab labels, equipment inventories, technical noticeboards

**Moments:**
1. Search results that make location and capability immediately scannable.
2. Lab detail pages where the hardware configuration reads like a precise equipment card, not marketing copy.

**Signature:** compact location chips such as `AB4 · 406` used consistently across search/results/details.

## Rules

- No purple/blue gradients, glowing backgrounds, glossy blobs, neon shadows, gradient text, emoji category labels, oversized marketing hero, or generic “AI dashboard” styling.
- Use quiet neutral surfaces, subtle hairline borders, and one restrained campus-blue accent.
- Use a 4px spacing grid. Default page padding 24–32px, card padding 20–24px, consistent list row heights.
- Keep typography sentence case. Avoid shouty all-caps labels and heavy 800/900-weight headings.
- Prefer one interface font, 400/500 weights with 600 only for rare emphasis. Use tabular numerals for RAM/GPU/room figures where useful.
- Use Lucide-style line icons if icons are needed. No emojis as interface icons.
- Navigation should be dense and quiet: Campus Connect, Explore, Labs, Admin. Do not turn every nav item into a pill/button.
- Cards should be informational rather than decorative. Location first, then system/resource name, then specs/tags/status.
- Search should dominate the home page because discovery is the core user need, but it should not be a giant empty landing-page hero.
- Copy must be specific to the campus problem. Use text such as `Find a GPU lab`, `Search labs, software or equipment`, `AB4 · 406`; avoid “Unlock campus resources” or other hype.
- Use real data from the committed source files. Never use fake testimonials, fake statistics, fake student names, or invented availability.
- For responsive layouts, preserve information hierarchy. On mobile stack filters above results; do not hide essential specs behind hover.
- Include deliberate loading, empty and error states rather than blank areas or generic spinners everywhere.

## Suggested visual system

- Page background: very light neutral/blue-grey, not pure white everywhere.
- Raised surfaces: white or near-white.
- Text: dark graphite/ink.
- Muted text: medium grey.
- Border: low-contrast graphite hairline.
- Accent: one dark/medium campus blue, used for selected filters, links and primary admin action.
- Semantic colors only for actual meaning (available, maintenance, error).
- Corner radius: restrained, around 8–12px; not every element needs a rounded container.
- Shadows: minimal/neutral or none.

## Home page

Header + concise intro + strong search field. Under it, show useful entry points rather than decorative KPI cards:
- GPU-capable labs
- Apple systems
- Embedded equipment
- Software by subject

Then show a compact `Browse by building` or `Popular searches` area using real source facts.

## Explore page

Desktop: compact filter column + fluid result list/grid. Important filters:
- building AB4 / AB5
- GPU available
- RAM range if useful
- type (lab/software/equipment)

Each lab result should emphasize:
`AB4 · 406` → `HP G9 Tower systems` → `16 GB RAM · 12 GB GPU`

Avoid giant icon cards.

## Lab detail

Treat this like an equipment/specification page:
- location
- current status if genuinely stored
- system configuration
- RAM/GPU values
- related verified data only

Do not show software under a lab unless a verified `lab_software` mapping is actually present.

## Admin

Admin is a tool, not a marketing page. Use compact tables/forms, clear destructive-action confirmation, inline validation, and restrained status feedback.

## Final UI check

Before opening the PR, audit the rendered pages for these common slop signs:
- unnecessary gradient/glow
- emoji-as-icon
- too many rounded cards
- excessive badges/pills
- generic copy
- huge empty hero
- inconsistent spacing
- random one-off colors
- excessive bold type
- centered long text
- decorative stats that do not help the task

Fix them before considering the frontend complete.
