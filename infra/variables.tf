variable "do_token" {
  description = "DigitalOcean API token"
  type        = string
  sensitive   = true
}

variable "region" {
  description = "DO region"
  type        = string
  default     = "fra1"
}

variable "ssh_key_ids" {
  description = "List of DO SSH key IDs to provision on droplets"
  type        = list(string)
}

variable "allowed_ssh_cidrs" {
  description = "CIDRs allowed to SSH. Never use 0.0.0.0/0"
  type        = list(string)
  default     = []
  validation {
    condition     = alltrue([for c in var.allowed_ssh_cidrs : c != "0.0.0.0/0"])
    error_message = "Public SSH (0.0.0.0/0) is forbidden."
  }
}

variable "app_port" {
  description = "Internal app port exposed by container"
  type        = number
  default     = 8080
}

variable "staging_size"  { 
    type = string
 default = "s-2vcpu-2gb" 
}
variable "production_size" { 
    type = string
 default = "s-4vcpu-8gb"
}

# DB settings
variable "enable_managed_db" { 
    type = bool
    default = true 
}
variable "db_engine"         { 
    type = string
 default = "pg" 
} # postgres
variable "db_version"        { 
    type = string
 default = "16" 
}
variable "db_name"           { 
    type = string
 default = "delivery_tracker" 
}

# Secrets are passed at deploy time via CI (never hardcode)
variable "app_env" {
  description = "Map of env vars to pass to the container (JWT_SECRET, DB creds, etc.)"
  type        = map(string)
  sensitive   = true
  default     = {}
}
