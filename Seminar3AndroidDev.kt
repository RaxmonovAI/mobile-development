

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController

// SECTION 1 — INTRODUCTION TO ANDROID STUDIO (Questions 1–9)

    // Q1: Android Studio is Google's official IDE for Android development (based on IntelliJ IDEA).
    // Four core things bundled into one workspace:
    //   1) Code & layout editor (Kotlin/Java, XML, Compose preview)
    //   2) Build system integration (Gradle)
    //   3) Device/emulator management (Device Manager & emulator runner)
    //   4) Debugging & profiling tools (Debugger, Logcat, Layout Inspector, Profiler)

    // Q2: Three reasons developers love Android Studio:
    //   1) Smart code assistance & refactoring: autocomplete, live error highlighting, safe renames/moves.
    //   2) Visual & layout tooling: instant UI previews, layout inspection, and resource management.
    //   3) Debugging & diagnostics: breakpoints, variable inspection, Logcat filtering, and profilers.

    // Q3: Four steps to install Android Studio (in order):
    //   1) Download the installer for your OS from the official Android developer site.
    //   2) Run the installer and follow the setup wizard prompts.
    //   3) Download/configure required Android SDK components, platform tools, and an emulator/system image.
    //   4) Open an existing project or start a new project to verify setup.
    // Recommended minimums: at least 8 GB RAM (16 GB recommended) and at least 8 GB free disk space
    // (16–20+ GB recommended once SDK platforms and emulators are installed).

    // Q4: Four settings configured in the New Project wizard (besides template):
    //   1) Name (project/app name)
    //   2) Package name
    //   3) Save location
    //   4) Minimum SDK

    // Q5: Six zones of the main Android Studio window:
    //   1) Toolbar: provides quick actions like Run, Debug, Gradle sync, and device selection.
    //   2) Project / Navigation bar: displays project files, modules, and directory hierarchy.
    //   3) Editor: displays and edits source code, XML files, or visual/split UI views.
    //   4) Tool windows: host auxiliary panels such as Logcat, Terminal, Gradle, and Build output.
    //   5) Status bar: shows background indexing/Gradle progress, current Git branch, and cursor/encoding info.
    //   6) Design / Preview pane: renders live visual previews of XML layouts or Compose UI.

    // Q6: In the Project panel's "Android" view:
    //   - manifests: contains AndroidManifest.xml (components, permissions, app metadata).
    //   - kotlin+java: contains Kotlin/Java source code and test source sets (main, test, androidTest).
    //   - res: contains UI and app resources (layout, drawable, values such as strings.xml/themes.xml, mipmap).

    // Q7: Basic completion (Ctrl+Space) vs Smart completion (Ctrl+Shift+Space):
    //   - Basic completion (Ctrl+Space) lists classes, methods, fields, and keywords valid in the current scope.
    //   - Smart completion (Ctrl+Shift+Space) filters suggestions to expressions/types matching the expected target type.

    // Q8: Breakpoint + Debug mode vs Run:
    //   - Debug mode pauses execution at breakpoints so you can inspect variables, call stacks, and step line-by-line;
    //     plain Run executes continuously without pausing at breakpoints.

    // Q9: Three of the five key tool windows & shortcuts:
    //   - Logcat (Alt+6 on Windows/Linux, Cmd+6 on macOS)
    //   - Terminal (Alt+F12 on Windows/Linux, Option+F12 on macOS)
    //   - Project (Alt+1 on Windows/Linux, Cmd+1 on macOS)

// SECTION 2 — INTRODUCTION TO GIT & GITHUB (Questions 10–24)

    // Q10: Version control tracks changes to files over time. Three things it lets you do:
    //   1) Keep history and revert/inspect past states.
    //   2) Work on separate branches/features in parallel and merge them.
    //   3) Collaborate with a team and share changes safely.

    // Q11: Git vs GitHub:
    //   - Git is a distributed version control system that runs locally on your machine to track snapshots and branches.
    //   - GitHub is a cloud hosting and collaboration platform for Git repositories offering PRs, issues, reviews, and Actions.

    // Q12: Configure Git identity (name and email):
    const val Q12_CONFIG_NAME = """git config --global user.name "Your Name""""
    const val Q12_CONFIG_EMAIL = """git config --global user.email "you@example.com""""

    // Q13: `git init` vs `git clone <url>`:
    //   - `git init` creates a new local repository in the current folder (used when starting a new repo from scratch).
    //   - `git clone <url>` copies an existing remote repository and its history locally (used when joining an existing project).

    // Q14: Four commands of the core Git workflow (in order):
    //   1) `git status` -> shows modified, staged, and untracked files.
    //   2) `git add <path>` -> stages changes for the next commit.
    //   3) `git commit -m "<message>"` -> records staged changes as a commit snapshot in local history.
    //   4) `git push` -> uploads local commits to the remote repository.

    // Q15: Connect remote, push commits, and pull/merge remote changes:
    const val Q15_REMOTE_ADD = "git remote add origin https://github.com/OWNER/REPO.git"
    const val Q15_PUSH = "git push -u origin main"
    const val Q15_PULL = "git pull origin main"

    // Q16: Create and switch to `feature/login` in a single command:
    const val Q16_CREATE_SWITCH_BRANCH = "git switch -c feature/login"

    // Q17: Merge conflict markers (`<<<<<<< HEAD`, `=======`, `>>>>>>> branch-name`):
    //   - Between `<<<<<<< HEAD` and `=======` is the code from your current HEAD branch.
    //   - Between `=======` and `>>>>>>> branch-name` is the incoming code from the branch being merged.
    //   - Before committing, edit the file to keep/combine the desired code, remove all conflict markers, and run `git add`.

    // Q18: Fix a commit message on a shared branch safely:
    const val Q18_AMEND = """git commit --amend -m "Corrected commit message""""
    const val Q18_FORCE_WITH_LEASE = "git push --force-with-lease"

    // Q19: Squash last 3 commits into 1 before opening a PR:
    //   1) Run `git rebase -i HEAD~3`.
    //   2) Keep `pick` on the first commit and change the next two to `squash` (or `s`).
    //   3) Save and close the editor, enter the combined commit message, and save.
    //   4) Push the updated branch with `git push --force-with-lease`.

    // Q20: Why Git LFS or a package registry instead of committing a 40 MB `.aar` directly:
    //   - Large binary artifacts bloat Git history, slow down clones/fetches, and cannot be meaningfully diffed;
    //     Git LFS stores large binaries out-of-band, and package registries distribute versioned artifacts cleanly.

    // Q21: Reviewer vs Assignee on a PR:
    //   - Reviewer is asked to inspect the code, leave feedback, and approve/request changes.
    //   - Assignee is the person responsible for owning/working on the PR or issue.

    // Q22: Label vs Milestone vs Project:
    //   - Label tags items by category/type/status (e.g., `bug`, `enhancement`).
    //   - Milestone groups issues/PRs toward a target date or release goal.
    //   - Project provides boards/tables and workflows to track status across many items/PRs.

    // Q23: Auto-close issues #42 and #17 when PR merges:
    const val Q23_CLOSE_ISSUES = "Closes #42, Closes #17"

    // Q24: Three things GitHub CLI (`gh`) does from the terminal, including opening a PR:
    //   1) Create and manage Pull Requests (`gh pr create --fill`)
    //   2) List/create/manage Issues (`gh issue list` / `gh issue create`)
    //   3) Inspect GitHub Actions workflow runs (`gh run list` / `gh run view`)
    const val Q24_CREATE_PR = "gh pr create --fill"

// SECTION 3 — ACTIVITY, FRAGMENTS & NAVIGATION (Questions 25–37)

// Q25: Activity lifecycle callbacks when opened, backgrounded, and returned to (without destruction):
//   1. onCreate()
//   2. onStart()
//   3. onResume()
//   4. onPause()
//   5. onStop()
//   6. onRestart()
//   7. onStart()
//   8. onResume()
//   (Note: the seven core lifecycle callbacks are onCreate, onStart, onResume, onPause, onStop, onRestart, onDestroy;
//    returning from background runs onRestart -> onStart -> onResume.)

// Q26: Three Fragment lifecycle callbacks without a direct Activity equivalent:
//   - onAttach(context): called when the Fragment is attached to its host context.
//   - onCreateView(inflater, container, savedInstanceState): inflates/creates the Fragment's view hierarchy.
//   - onViewCreated(view, savedInstanceState): called right after view creation to bind views and observe UI state.

// Q27: Fragments generally have a lower memory footprint than launching a new Activity because they live inside
//      an existing Activity and avoid creating a separate heavyweight Activity instance and full window overhead.

// Q28: Fragments enable more seamless UI transitions by swapping or animating views/transactions within the same
//      Activity (including shared element transitions) without recreating an entire Activity.

// Q29: Tablet example: a master-detail (two-pane) layout showing a list of items on the left and the selected
//      item's details on the right simultaneously without navigating to a separate screen.

// Q30: Navigation Component centralizes navigation paths, back-stack handling, argument passing, and deep links
//      instead of manual `FragmentTransaction` calls. Routes are defined in a navigation graph XML file
//      (such as `res/navigation/nav_graph.xml`).

// Q31: Two Gradle dependencies for Navigation Component:
//   - `androidx.navigation:navigation-fragment-ktx`
//   - `androidx.navigation:navigation-ui-ktx`
// Safe Args adds a Gradle code-generation plugin that generates type-safe `*Directions` and `*Args` classes
// for navigating with arguments without raw string keys or manual `Bundle` lookups.

// Q32: In `nav_graph.xml`:
//   - `startDestination`: the initial destination shown when the graph opens.
//   - `<fragment>`: defines a Fragment destination in the navigation graph.
//   - `<action>`: defines a navigation transition between destinations (with optional animations/arguments).

// Q33: A `NavHost` is the container where navigation destinations are swapped in and out; in `activity_main.xml`,
//      `androidx.fragment.app.FragmentContainerView` (with `app:navGraph` and `app:defaultNavHost="true"`) hosts it.

// Q34: Navigating to `detailFragment` without Safe Args and with Safe Args:
class ListFragment : Fragment() {
    fun navigateWithoutSafeArgs() {
        // Without Safe Args (using action ID):
        // findNavController().navigate(R.id.action_listFragment_to_detailFragment)
        findNavController()
    }

    fun navigateWithSafeArgs(itemId: String) {
        // With Safe Args (generated `ListFragmentDirections`):
        // val action = ListFragmentDirections.actionListFragmentToDetailFragment(itemId)
        // findNavController().navigate(action)
        println(itemId)
    }
}

// Q35: Reading `itemId` in `DetailFragment` via Safe Args:
// In `nav_graph.xml` inside `<fragment android:id="@+id/detailFragment" ...>`:
//   <argument
//       android:name="itemId"
//       app:argType="string" />
class DetailFragment : Fragment() {
    // When Safe Args is enabled:
    // private val args: DetailFragmentArgs by navArgs()
    // fun readItemId(): String = args.itemId
    fun exampleArgumentAccess(arguments: Bundle?): String? {
        return arguments?.getString("itemId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}

// Q36: Connecting Toolbar Up button and system Back button with `NavController`:
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment =
            supportFragmentManager.findFragmentById(android.R.id.content) as? NavHostFragment
        navHostFragment?.let {
            navController = it.navController
            setupActionBarWithNavController(navController)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return (::navController.isInitialized && navController.navigateUp()) || super.onSupportNavigateUp()
    }
}

// Q37: `<deepLink>` inside a `<fragment>` destination lets external links/URIs (such as
//      `https://example.com/item/{id}` or `android-app://androidx.navigation/...`) open the app directly to that
//      destination, populating matching arguments automatically.
