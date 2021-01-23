import VueRouter from "vue-router";

/**
 * Lazily load components
 */
const Home = () => import("@/views/Home.vue");
const Login = () => import("@/views/Login.vue");
const Signup = () => import("@/views/Signup.vue");
const Profile = () => import("@/views/Profile.vue");
const EditProfile = () => import("@/views/EditProfile.vue");
const Error = () => import("@/views/Error.vue");
const CreateOrEditActivity = () => import("@/views/CreateOrEditActivity.vue");
const ViewActivity = () => import("@/views/ViewActivity.vue");
const SearchUsers = () => import("@/views/SearchUsers.vue");
const AdminDashboard = () => import("@/views/AdminDashboard.vue");
const DashboardActivities = () =>
  import("@/components/Dashboard/Activities.vue");
const ActivityErrorPage = () => import("@/views/ActivityErrorPage.vue")
const HomeFeed = () => import("@/views/HomeFeed.vue");
const ActivityOutcomeAnswer = () =>
  import("@/components/Activity/ActivityOutcomeAnswer");
const MapPage = () => import("@/views/MapPage.vue")
const ImageUpload = () => import("@/components/Activity/ImageUpload");

const Yeet = () => import("@/components/Activity/ActivitySearchCard");


import { ToastProgrammatic as Toast } from "buefy";
import store from "@/store/store";
import api from "@/Api";

const routes = [
  {
    path: "/yeet/:activityId",
    name: "yeet",
    props:true,
    component: Yeet,
  },
  {
    path: "/",
    name: "home",
    component: Home,
  },
  {
    path: "/login",
    name: "login",
    component: Login,
  },
  {
    path: "/signup",
    name: "signup",
    component: Signup,
  },
  {
    name: "logout",
    path: "/logout",
  },
  {
    path: "/profile/:userId/activity/:activityId/answer",
    name: "activityOutcomeAnswer",
    props: true,
    component: ActivityOutcomeAnswer,
    meta: {
      requiresAuth: true
    },
  },
  {
    path: "/profiles/search",
    name: "SearchUsers",
    component: SearchUsers,
    props: true,
    meta: {
      requiresAuth: true,
    },
  },
  {
    name: "profile",
    path: "/profiles/:userId",
    component: Profile,
    props: true,
    meta: {
      requiresAuth: true,
    },
  },
  {
    name: "profileEdit",
    path: "/profiles/:userId/edit",
    component: EditProfile,
    props: true,
    meta: {
      requiresAuth: true,
      selfOnly: true,
    },
  },
  {
    name: "createActivity",
    path: "/profiles/:userId/activities/create",
    component: CreateOrEditActivity,
    props: true,
    meta: {
      requiresAuth: true,
      selfOnly: true,
    },
  },
  {
    name: "editActivity",
    path: "/profiles/:userId/activities/:activityId/edit",
    component: CreateOrEditActivity,
    props: true,
    meta: {
      requiresAuth: true,
      organiserOrCreatorOnly: true,
    },
  },
  {
    name: "SearchActivities",
    path: "/activities/search",
    component: MapPage,
    props: (route) => ({
      load: Boolean(route.query.load)
    }),
    meta: {
      requiresAuth: true,
    },
  },
  {
    name: "ViewActivity",
    path: "/activities/:activityId",
    component: ViewActivity,
    props: true,
    meta: {
      requiresAuth: true,
    },
  },
  {
    name: "ImageUpload",
    path: "/activities/:activityId/images",
    component: ImageUpload,
    props: true,
    meta: {
      requiresAuth: true,
    },
  },
  {
    name: "adminDashboard",
    path: "/admin/dashboard",
    component: AdminDashboard,
    props: true,
    meta: {
      requiresAuth: true,
      adminOnly: true,
    },
    children: [
      {
        name: "dashboardSearch",
        path: "search",
        component: SearchUsers,
        props: { fromAdminDashboard: true, fromSearchResults: false },
      },
      {
        name: "dashboardActivities",
        path: "activities",
        component: DashboardActivities,
      },
    ],
  },
  {
    name: "homeFeed",
    path: "/profile/:userId/homeFeed",
    props: true,
    component: HomeFeed,
    meta: {
      requiresAuth: true,
      selfOnly: true,
    },
  },
  {
    path: "/error",
    name: "error",
    component: Error,
  },
  {
    path: "/activity-error",
    name: "activityError",
    component: ActivityErrorPage,
  },
  {
    path: "*",
    redirect: "/error",
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.VUE_APP_BASE_URL,
  routes: routes,
});

/**
 * Handler if the next page is for current user or admin only
 *
 * @params to:   the target Route Object being navigated to
 * @params next: function to be called to resolve the hook
 */
function handleAdminOnly(to, next) {
  const state = store.state;
  if (state.isGlobalAdmin || state.isAdmin) {
    next();
  } else {
    Toast.open({
      message: "Oops! You're not allowed to view that page",
      type: "is-warning",
      duration: 1000,
    });
    next({ name: "profile", params: { userId: state.userId } });
  }
}

/**
 * Handler if the next page is for current user or admin only
 *
 * @params to:   the target Route Object being navigated to
 * @params next: function to be called to resolve the hook
 */
function handleSelfOnly(to, next) {
  const state = store.state;
  if (Number(to.params.userId) === Number(state.userId)) {
    next();
  } else {
    Toast.open({
      message: "Oops! You're not allowed to view that page",
      type: "is-warning",
      duration: 1000,
    });
    next({ name: "profile", params: { userId: state.userId } });
  }
}

/**
 * Handler if the next page is restricted to activity owners/organisers
 *
 * @params to:   the target Route Object being navigated to
 * @params next: function to be called to resolve the hook
 */
function handleOrganiserOrCreatorOnly(to, next) {
  const state = store.state;
  api
    .getActivityUserRelation(to.params.activityId)
    .then((response) => {
      const role = response.data.role;
      if (role === "creator" || role === "organiser") {
        next();
      } else {
        next({ name: "profile", params: { userId: state.userId } });
      }
    })
    .catch(() => {
      next({ name: "profile", params: { userId: state.userId } });
    });
}

/**
 * Handles the authentication of page redirection
 *
 * @params to:   the target Route Object being navigated to
 * @params next: function to be called to resolve the hook
 */
function handleAuth(to, next) {
  const selfOnly = to.matched.some((r) => r.meta.selfOnly);
  const adminOnly = to.matched.some((r) => r.meta.adminOnly);
  const organiserOrCreatorOnly = to.matched.some(
    (r) => r.meta.organiserOrCreatorOnly
  );
  const state = store.state;

  if (!state.isLoggedIn) {
    next({ name: "login" });
  }

  if (state.isGlobalAdmin || state.isAdmin) {
    // Allow admins in anywhere
    next();
  } else if (adminOnly) {
    handleAdminOnly(to, next);
  } else if (selfOnly) {
    handleSelfOnly(to, next);
  } else if (organiserOrCreatorOnly) {
    handleOrganiserOrCreatorOnly(to, next);
  } else {
    // No auth required
    next();
  }
}

/**
 * Defines guards between page navigations
 *
 * @params to:   the target Route Object being navigated to
 * @params from: the current route being navigated away from
 * @params next: function to be called to resolve the hook
 */
router.beforeEach((to, _from, next) => {
  const state = store.state;
  const requiresAuth = to.matched.some((r) => r.meta.requiresAuth);

  sessionStorage.setItem("from", _from.name);

  if (_from.name === "SearchUsers" && to.name != "SearchActivities") {
    store.dispatch("setFromUserSearch", true);
  } else {
    store.dispatch("setFromUserSearch", false);
  }

  if (_from.name === "SearchActivities" && to.name != "SearchUsers") {
    store.dispatch("setFromActivitySearch", true);
  } else {
    store.dispatch("setFromActivitySearch", false);
  }
  if (requiresAuth) {
    // Do auth check if user is not already logged in
    if (!state.isLoggedIn) {
      store.dispatch("checkAuth").then(() => {
        handleAuth(to, next);
      });
    } else {
      handleAuth(to, next);
    }
  } else {
    // No auth required
    next();
  }
});

export default router;
