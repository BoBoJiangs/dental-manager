import { nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

type QueryState = Record<string, string | number | undefined | null>

export function useRouteQueryState<T extends QueryState>(state: T, defaults?: Partial<T>) {
  const route = useRoute()
  const router = useRouter()

  Object.keys(state).forEach((key) => {
    const routeValue = route.query[key]
    if (routeValue !== undefined) {
      ;(state as Record<string, any>)[key] = routeValue
    } else if (defaults && key in defaults) {
      ;(state as Record<string, any>)[key] = defaults[key as keyof T]
    }
  })

  watch(
    () => ({ ...state }),
    async (value) => {
      await nextTick()
      const query = { ...route.query } as Record<string, any>
      Object.entries(value).forEach(([key, item]) => {
        if (item === undefined || item === null || item === '') {
          delete query[key]
        } else {
          query[key] = String(item)
        }
      })
      router.replace({ query })
    },
    { deep: true },
  )
}
