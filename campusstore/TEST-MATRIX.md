# Test Matrix — Campus Store

| ID  | Scenario | Steps | Expected Result | Actual Result |
|-----|----------|-------|-----------------|---------------|
| T01 | Admin login | Log in as admin123@campusstore.com / Admin@123, open admin dashboard | Admin dashboard displays with categories, products, and orders sections | Admin dashboard loaded successfully showing all sections |
| T02 | Admin creates category | Log in as admin, enter "Books" in category field, click Add Category | New category "Books" appears in the category list | Category "Books" appeared immediately in the list |
| T03 | Admin product lifecycle | Create product "Test Item" → edit its price → click Deactivate; check catalog | Product appears after create, price updates after edit, product disappears from catalog after deactivate | Product created, edited, and deactivated successfully; disappeared from catalog |
| T04 | Customer registration | Register with a new @example.com address; confirm redirect to login | Redirect to login page with success message | Redirected to login with message "Registration successful" |
| T05 | Catalog F/S/P | Use name filter, categoryId filter, inStock filter; change sortBy/sortDir; navigate pages | Filtered results update correctly; sort order changes; page 0 and page 1 both return results | All filters, sort, and pagination worked correctly |
| T06 | Create multi-item order | Add qty > 0 for at least 2 different products; click Place Order | Confirmation message showing Order ID and Total | Order placed successfully, confirmation showed Order ID and Total = correct amount |
| T07 | Stock deducted + totals correct | After placing order, check stock of ordered products in catalog | stockQty reduced by ordered qty; total matches server-computed value | Stock reduced correctly; total matched expected value |
| T08 | My order history + details | Open "my orders" list; open one order detail page | Order list shows placed orders; detail page shows correct items and total | Order history displayed correctly; detail showed correct items and total |
| T09 | Forbidden — wrong role | Log in as CUSTOMER; navigate to /admin/dashboard directly | Forbidden page shown (HTTP 403) | Forbidden page displayed correctly |
| T10 | Forbidden — wrong owner | As Customer A, paste Customer B's order detail URL | Forbidden page shown (HTTP 403) | Forbidden page displayed correctly |
| T11 | Admin status update + cancel restores stock | Set order NEW → CANCELLED; confirm stock restored. Set different order NEW → FULFILLED; confirm it cannot be reverted | Stock restored after cancel; FULFILLED order shows terminal state | Stock restored correctly after cancellation; FULFILLED order showed terminal state and could not be changed |