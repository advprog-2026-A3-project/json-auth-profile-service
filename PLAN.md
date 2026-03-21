# Context & Agent Instructions

You are an expert Backend Developer AI. You are working on the **Auth & Profile Microservice** for a larger system. Your scope is strictly BACKEND (Database migrations, Models, Controllers, Routes/Endpoints, and Business Logic). Do NOT write any frontend code.

## Workflow Rules
1. Read the `[ ] Tasks` list below.
2. Find the first task that is not checked `[ ]`.
3. Implement the required code for that task.
4. If a task is already marked as `[x]`, DO NOT touch it or reimplement it. Leave it alone.
5. Once you finish implementing a task, update this `plan.md` file by changing `[ ]` to `[x]` for that specific task.
6. Stop and wait for the next prompt/approval.

---

# Module: Authentication & Profile Service

**Description:** Main gateway for user activity. Manages identity, access security, and role validation (Admin, Jastiper, Titipers). Default role is Titipers.

## Business Rules & Edge Cases to Handle:
- **Username Auto-generation:** If a user updates their profile without providing a username, the system MUST auto-generate it (e.g., extract the local part of the email: `budi@gmail.com` -> `budi`).
- **Roles:** `Admin`, `Jastiper`, `Titipers`.
- **Status:** `Active`, `Banned`, `Pending Verification` (for KYC).
- **Cross-Service Logic Note:** Jastipers cannot buy their own items. (Since this is an Auth service, just ensure the `userId` and `role` are easily verifiable via JWT/Token payload so the Transaction Microservice can handle that block).

---

## Tasks

### Phase 1: Setup & Authentication
- [x] **Task 1: Database Schema Setup**
  - Create migrations/models for `Users` (id, email, password, role, status), `Profiles` (user_id, username, full_name, stats), and `KycRequests` (user_id, full_name, id_card_data, social_media_link, status).
- [x] **Task 2: Register Endpoint**
  - Endpoint: `POST /api/auth/register`
  - Input: `email`, `password`.
  - Logic: Hash password, create user with default role `Titipers` and status `Active`.
- [x] **Task 3: Login Endpoint**
  - Endpoint: `POST /api/auth/login`
  - Input: `email`, `password`.
  - Logic: Validate credentials, return JWT/Auth Token containing `userId`, `role`, and `status`.

### Phase 2: Profile Management
- [x] **Task 4: Update Profile Endpoint**
  - Endpoint: `PUT /api/profile`
  - Auth required.
  - Input: `username` (optional), `full_name`.
  - Logic: Ensure `username` is unique. Implement auto-generate username logic if `username` is empty.

### Phase 3: KYC & Verification (Jastiper Upgrade)
- [x] **Task 5: Submit KYC Endpoint**
  - Endpoint: `POST /api/profile/kyc`
  - Auth required.
  - Input: `full_name` (must match ID), `social_media_link` (or other verification data).
  - Logic: Create KYC request record. Update user status/flag to `Pending Verification`.
- [x] **Task 6: Admin Validation Endpoint**
  - Endpoint: `POST /api/admin/kyc/:id/validate`
  - Auth required: `Admin` only.
  - Input: `action` (Approve/Reject).
  - Logic: If approved, update user role to `Jastiper` (or add badge). Update KYC status.

### Phase 4: Data Retrieval & Monitoring
- [x] **Task 7: Get Public Profile Endpoint**
  - Endpoint: `GET /api/profile/:id`
  - Logic: Return public profile data.
    - If user is `Jastiper`: Include extra info (e.g., successful transaction stats).
    - If user is `Titipers`: Return basic info and verification status.
- [x] **Task 8: Admin User Monitoring Endpoint**
  - Endpoint: `GET /api/admin/users`
  - Auth required: `Admin` only.
  - Logic: Return list of all users with their roles and account statuses (`Active`, `Banned`, `Pending`). Include pagination and basic filtering by status.