# Frontend review

Reviewed with [unslop by Damir Sokolovsky](https://github.com/heydamir/unslop/tree/main/skills/unslop), CC BY 4.0. The skill and references were loaded through the connected GitHub tool and installed at `/tmp/campus-skills/unslop` for this session after the standard installer could not resolve GitHub from the shell.

## Direction

Product: campus resource discovery for students looking for labs, hardware, subject software and equipment.
Character: practical, exact, quietly academic.
World: campus directories, lab labels and equipment inventories.
Moments: location-first GPU results and hardware specifications with clearly set RAM/GPU figures.
Signature: `AB4 · 406` location labels with a narrow campus-blue rule, repeated on results and details.

UI_DIRECTION.md already specifies this direction and the user authorized implementation and unslopping, so no additional direction approval was needed. Campus Connect is a directory application; real equipment data supplies the visual interest. Generated hero imagery would compete with discovery and is omitted.

## Audit and passes

There was no prior frontend to screenshot. Review the implemented pages against the skill's signs checklist:

1. Spacing: 4px grid; 24–32px page edges, 20–24px resource padding, equal row padding and aligned specification columns. Mobile uses stacked filters and results, with a locally scrollable admin table.
2. Color: one near-neutral ramp, three text roles, white inventory surfaces and campus blue for actions/location labels. Errors use muted red. No hue gradients, glows or colored shadows.
3. Icons: unnecessary icons omitted. No emoji interface labels; textual arrows indicate navigation.
4. Copy: campus-specific search, verified GPU/Apple locations and explicit limitations on software installations and occupancy. No invented statistics, testimonials or source locations.
5. Type: one system sans family, weights 400/500, restrained headings, sentence case, tabular RAM/GPU/room figures. No external font requests.
6. Layout: compact search-first home; numbered resource index and building directory; fluid inventory results; equipment-card detail; compact admin table/forms. No giant landing hero or repeated decorative cards.
7. Details: 8px controls/panels, hairline separators, visible keyboard focus, skip link, accessible field labels and deliberate loading/empty/error feedback. Delete actions ask for explicit confirmation.
8. Visuals: real DB-backed GPU resource rows and specification grids are the product moments. No generated decorative imagery or filler charts.
9. Motion: only short button background feedback; reduced-motion override, no looping animation, scaling cards or animated layout.

## Render evidence

Playwright captures desktop/mobile home, GPU results, lab detail and admin screens during the full-flow suite. Rendered review status is recorded in `VERIFICATION.md`; source review alone is not a rendered audit.
